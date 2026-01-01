package com.example.Leave.Management.controllers;


import com.example.Leave.Management.dtos.SupervisorMemberDtos.RegisterRelationshipRequest;
import com.example.Leave.Management.dtos.SupervisorMemberDtos.UpdateRelationshipRequest;
import com.example.Leave.Management.entities.SupervisorMember;
import com.example.Leave.Management.entities.SupervisorType;
import com.example.Leave.Management.exceptions.UserNotFoundException;
import com.example.Leave.Management.mappers.SupervisorMemberMapper;
import com.example.Leave.Management.repositories.SupervisorMemberRepository;
import com.example.Leave.Management.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/supervisor-relationship")
public class SupervisorMemberController {

    private final UserRepository userRepository;
    private final SupervisorMemberRepository supervisorMemberRepository;
    private final SupervisorMemberMapper supervisorMemberMapper;

    @PostMapping
    public ResponseEntity<?> createARelationship(@Valid @RequestBody RegisterRelationshipRequest request , UriComponentsBuilder uriBuilder){
        SupervisorMember relationship = new SupervisorMember();
        var user = userRepository.findById(request.getUser()).orElseThrow(UserNotFoundException::new);
        var supervisor = userRepository.findById(request.getSupervisor()).orElseThrow(UserNotFoundException::new);

        if(request.getType() == SupervisorType.PRIMARY){
            var existsPrimary = supervisorMemberRepository.findByUserAndType(user , SupervisorType.PRIMARY);
            if(existsPrimary.isPresent()){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already have a primary supervisor");
            }
        }
        relationship.setUser(user);
        relationship.setSupervisor(supervisor);
        relationship.setType(request.getType());
        var saved =  supervisorMemberRepository.save(relationship);
        var response = supervisorMemberMapper.toResponse(saved);
        var uri = uriBuilder.path("/leave-day-type/{id}").buildAndExpand(response.getId()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRelationship(@PathVariable(name = "id") Long id){
        var relationship = supervisorMemberRepository.findById(id).orElse(null);
        if(relationship == null){
            return ResponseEntity.notFound().build();
        }

        supervisorMemberRepository.delete(relationship);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateRelationship(@RequestBody UpdateRelationshipRequest request , @PathVariable(name="id") Long id){
        var relationship = supervisorMemberRepository.findById(id).orElse(null);
        if(relationship == null){
            return ResponseEntity.notFound().build();
        }
        var supervisor = request.getSupervisor();
        var type = request.getType();

        if(supervisor != null){
            if(type !=null){
                if(type == SupervisorType.PRIMARY){
                    var existsPrimary = supervisorMemberRepository.findByUserAndTypeForUpdate(relationship.getUser() , SupervisorType.PRIMARY , id);
                    if(!existsPrimary.isEmpty()){
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already have a primary supervisor");
                    }
                    relationship.setType(type);
                }else{
                    relationship.setType(type);
                }
            }
                var supervisorUser = userRepository.findById(supervisor).orElseThrow(UserNotFoundException::new);
                if (relationship.getUser().getId().equals(supervisor)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Can't set user as his own supervisor");
                }
                relationship.setSupervisor(supervisorUser);
        }else{
            if(type !=null){
                if(type == SupervisorType.PRIMARY){
                    var existsPrimary = supervisorMemberRepository.findByUserAndTypeForUpdate(relationship.getUser() , SupervisorType.PRIMARY , id);
                    if(!existsPrimary.isEmpty()){
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already have a primary supervisor");
                    }
                    relationship.setType(type);
                }else{
                    relationship.setType(type);
                }
            }
        }
        supervisorMemberRepository.save(relationship);
        return ResponseEntity.ok().build();
    }


}
