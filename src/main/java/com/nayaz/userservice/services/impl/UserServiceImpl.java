package com.nayaz.userservice.services.impl;

import com.nayaz.userservice.entities.Hotel;
import com.nayaz.userservice.entities.Rating;
import com.nayaz.userservice.exceptions.ResourceNotFoundException;
import com.nayaz.userservice.external.services.HotelService;
import com.nayaz.userservice.repositories.UserRepository;
import com.nayaz.userservice.services.UserService;
import com.nayaz.userservice.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        for(User user : users) {
            Rating[] ratingOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
            List<Rating> ratings = Arrays.stream(ratingOfUser).toList();
            logger.info(" {} ", ratings);

            List<Rating> ratingList = ratings.stream().map(rating -> {
                //ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
                //Hotel hotel = forEntity.getBody();
                Hotel hotel = hotelService.getHotel(rating.getHotelId());
                //logger.info("Response status code : {} ", forEntity.getStatusCode());
                rating.setHotel(hotel);
                return  rating;
            }).collect(Collectors.toList());

            user.setRatings(ratingList);
        }
        return users;
    }

    @Override
    public User getUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !! :" + userId));
        Rating[] ratingOfUser = restTemplate.getForObject("http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);
        List<Rating> ratings = Arrays.stream(ratingOfUser).toList();
        logger.info(" {} ", ratings);

        List<Rating> ratingList = ratings.stream().map(rating -> {
            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            Hotel hotel = forEntity.getBody();
            logger.info("Response status code : {} ", forEntity.getStatusCode());
            rating.setHotel(hotel);
            return  rating;
        }).collect(Collectors.toList());

        user.setRatings(ratingList);
        return  user;
    }

    @Override
    public User updateUser(String userId, User user) {
        User updateUser = userRepository.findById(userId).get();
        updateUser.setName(user.getName());
        updateUser.setEmail(user.getEmail());
        updateUser.setAbout(user.getAbout());
        return userRepository.save(updateUser);
    }

    @Override
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


}
