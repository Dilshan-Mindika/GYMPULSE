package com.nexus.GYMPULSE.repositories;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nexus.GYMPULSE.model.person.Member;

@Repository
public interface MemberRepository extends MongoRepository<Member, ObjectId> {
    Optional<Member> findByMemberId(String memberId);
}
