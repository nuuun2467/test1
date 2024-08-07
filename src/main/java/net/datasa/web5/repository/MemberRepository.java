package net.datasa.web5.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.datasa.web5.domain.entity.MemberEntity;

//findbyId, deletebyId 사용할떄 사용
public interface MemberRepository extends JpaRepository<MemberEntity, String> {

}
