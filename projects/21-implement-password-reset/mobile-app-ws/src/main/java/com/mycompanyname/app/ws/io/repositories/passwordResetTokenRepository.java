package com.mycompanyname.app.ws.io.repositories;

import org.springframework.data.repository.CrudRepository;

import com.mycompanyname.app.ws.io.entity.PasswordResetTokenEntity;

public interface passwordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long>{
}
