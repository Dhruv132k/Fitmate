package com.fitmate.user;

import com.fitmate.common.NotFoundException;
import com.fitmate.user.dto.UpdateProfileRequest;
import com.fitmate.user.dto.UserProfileResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long id) {
        return UserProfileResponse.from(getById(id));
    }

    /** Updating the profile changes match relevance, so the user's cached feed is evicted. */
    @Transactional
    @CacheEvict(cacheNames = "discoveryFeed", key = "#id")
    public UserProfileResponse updateProfile(Long id, UpdateProfileRequest req) {
        User user = getById(id);
        if (req.name() != null) user.setName(req.name());
        if (req.age() != null) user.setAge(req.age());
        if (req.bio() != null) user.setBio(req.bio());
        if (req.workoutGoal() != null) user.setWorkoutGoal(req.workoutGoal());
        if (req.experienceLevel() != null) user.setExperienceLevel(req.experienceLevel());
        if (req.gymName() != null) user.setGymName(req.gymName());
        if (req.city() != null) user.setCity(req.city());
        if (req.latitude() != null) user.setLatitude(req.latitude());
        if (req.longitude() != null) user.setLongitude(req.longitude());
        return UserProfileResponse.from(userRepository.save(user));
    }
}
