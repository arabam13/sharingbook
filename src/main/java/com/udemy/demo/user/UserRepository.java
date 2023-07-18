package com.udemy.demo.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jdbc.repository.query.Query;

@Repository
public interface UserRepository extends CrudRepository<UserInfo, Integer> {
    @Query("SELECT * FROM user_info WHERE email=:email")
    UserInfo findOneByEmail(String email);

    @Query("SELECT email, first_name, last_name FROM user_info WHERE id=:userId")
    UserInfo findByIdWithoutPassword(Integer userId);

}