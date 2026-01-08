package com.elearning.services;

import com.elearning.entities.Course;
import com.elearning.entities.Enrollment;
import com.elearning.entities.Material;
import com.elearning.entities.Topic;
import com.elearning.entities.User;
import com.elearning.entities.ELearningException;
import com.elearning.entities.ELearningFacadeRemote;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless(name = "ELearningFacade")
public class ELearningFacade implements ELearningFacadeRemote {

    @PersistenceContext(unitName = "ELearning-PU")
    private EntityManager em;

    // --- Course Methods ---
    @Override
    public Course[] getAllCourses() throws ELearningException {
        // Fetch all courses
        Query query = em.createQuery("SELECT c FROM Course c");
        List<Course> list = query.getResultList();
        return list.toArray(new Course[0]);
    }

    @Override
    public Course[] getCoursesByInstructor(String username) throws ELearningException {
        Query query = em.createQuery("SELECT c FROM Course c WHERE c.instructorId = :user");
        query.setParameter("user", username);
        List<Course> list = query.getResultList();
        return list.toArray(new Course[0]);
    }

    @Override
    public Course getCourse(String code) throws ELearningException {
        Course course = em.find(Course.class, code);
        if (course == null) throw new ELearningException("Course not found: " + code);
        return course;
    }

    @Override
    public void addCourse(Course course) throws ELearningException {
        em.persist(course);
    }

    @Override
    public void deleteCourse(String code) throws ELearningException {
        // FIXED: Delete children first to prevent Foreign Key constraints violation
        Course c = em.find(Course.class, code);
        if (c != null) {
            // 1. Delete all Enrollments for this course
            Query qEnroll = em.createQuery("DELETE FROM Enrollment e WHERE e.courseCode = :code");
            qEnroll.setParameter("code", code);
            qEnroll.executeUpdate();

            // 2. Handle Topics and Materials associated with this course
            // We must fetch topics first to delete their specific materials
            Query qTopics = em.createQuery("SELECT t FROM Topic t WHERE t.courseCode = :code");
            qTopics.setParameter("code", code);
            List<Topic> topics = qTopics.getResultList();

            for (Topic t : topics) {
                // Delete materials for this specific topic
                Query qMat = em.createQuery("DELETE FROM Material m WHERE m.topicId = :tid");
                qMat.setParameter("tid", t.getTopicId());
                qMat.executeUpdate();

                // Delete the topic itself
                em.remove(t);
            }

            // 3. Finally, delete the Course
            em.remove(c);
        } else {
            throw new ELearningException("Cannot delete. Course not found.");
        }
    }

    @Override
    public void updateCourse(Course course) throws ELearningException {
        Course existing = em.find(Course.class, course.getCourseCode());
        if (existing != null) {
            existing.setCourseName(course.getCourseName());
            existing.setCredits(course.getCredits());
            existing.setDescription(course.getDescription());
            existing.setContentUrl(course.getContentUrl());
            // Note: We do NOT update status here to prevent accidental overrides
            em.merge(existing);
        } else {
            throw new ELearningException("Cannot update. Course not found.");
        }
    }

    // [NEW] Update Status
    @Override
    public void updateCourseStatus(String code, String status) throws ELearningException {
        Course existing = em.find(Course.class, code);
        if (existing != null) {
            existing.setStatus(status);
            em.merge(existing);
        } else {
            throw new ELearningException("Course not found.");
        }
    }

    // --- User Methods ---
    @Override
    public void registerUser(User user) throws ELearningException {
        User existing = em.find(User.class, user.getUsername());
        if (existing != null) throw new ELearningException("Username taken.");
        em.persist(user);
    }

    @Override
    public User validateUser(String username, String password) throws ELearningException {
        User user = em.find(User.class, username);
        if (user != null && user.getPassword().equals(password)) return user;
        return null;
    }

    @Override
    public User[] getAllUsers() throws ELearningException {
        Query q = em.createQuery("SELECT u FROM User u");
        List<User> list = q.getResultList();
        return list.toArray(new User[0]);
    }

    @Override
    public void deleteUser(String username) throws ELearningException {
        User u = em.find(User.class, username);
        if (u != null) {
            Query q = em.createQuery("DELETE FROM Enrollment e WHERE e.username = :user");
            q.setParameter("user", username);
            q.executeUpdate();
            em.remove(u);
        } else {
            throw new ELearningException("User not found.");
        }
    }

    // --- Enrollment Methods ---
    @Override
    public boolean isEnrolled(String username, String courseCode) {
        Query q = em.createQuery("SELECT e FROM Enrollment e WHERE e.username = :user AND e.courseCode = :code");
        q.setParameter("user", username);
        q.setParameter("code", courseCode);
        return !q.getResultList().isEmpty();
    }

    @Override
    public void enrollStudent(String username, String courseCode) throws ELearningException {
        if (isEnrolled(username, courseCode)) throw new ELearningException("Already enrolled.");
        Enrollment enrollment = new Enrollment(username, courseCode);
        em.persist(enrollment);
    }

    @Override
    public Course[] getEnrolledCourses(String username) throws ELearningException {
        Query q = em.createQuery("SELECT e.courseCode FROM Enrollment e WHERE e.username = :user");
        q.setParameter("user", username);
        List<String> codes = q.getResultList();
        List<Course> courses = new ArrayList<>();
        for (String code : codes) {
            Course c = em.find(Course.class, code);
            if (c != null) courses.add(c);
        }
        return courses.toArray(new Course[0]);
    }

    @Override
    public void dropCourse(String username, String courseCode) throws ELearningException {
        try {
            Query q = em.createQuery("SELECT e FROM Enrollment e WHERE e.username = :user AND e.courseCode = :code");
            q.setParameter("user", username);
            q.setParameter("code", courseCode);
            List<Enrollment> results = q.getResultList();
            if (!results.isEmpty()) em.remove(results.get(0));
        } catch (Exception ex) {
            throw new ELearningException("Error dropping course.");
        }
    }

    // --- Topic & Material Logic ---
    @Override
    public void addTopic(Topic topic) throws ELearningException { em.persist(topic); }
    @Override
    public List<Topic> getTopicsByCourse(String courseCode) throws ELearningException {
        Query q = em.createQuery("SELECT t FROM Topic t WHERE t.courseCode = :code ORDER BY t.topicId ASC");
        q.setParameter("code", courseCode);
        return q.getResultList();
    }
    @Override
    public Topic getTopic(int topicId) throws ELearningException { return em.find(Topic.class, topicId); }
    @Override
    public void deleteTopic(int topicId) throws ELearningException {
        Topic t = em.find(Topic.class, topicId);
        if (t != null) {
             Query q = em.createQuery("DELETE FROM Material m WHERE m.topicId = :tid");
             q.setParameter("tid", topicId);
             q.executeUpdate();
             em.remove(t);
        }
    }
    @Override
    public void addMaterial(Material material) throws ELearningException { em.persist(material); }
    @Override
    public List<Material> getMaterialsByTopic(int topicId) throws ELearningException {
        Query q = em.createQuery("SELECT m FROM Material m WHERE m.topicId = :tid ORDER BY m.materialId ASC");
        q.setParameter("tid", topicId);
        return q.getResultList();
    }
    @Override
    public Material getMaterial(int mid) throws ELearningException { return em.find(Material.class, mid); }
    @Override
    public void deleteMaterial(int mid) throws ELearningException {
        Material m = em.find(Material.class, mid);
        if(m != null) em.remove(m);
    }
}