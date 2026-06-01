package com.example.Backend.Controller;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.RegisterSession;
import com.example.Backend.Service.RegisterSessionService;
import com.example.Backend.Service.SalesitemService;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/register-session")
@AllArgsConstructor
public class RegisterSessionController {

    private final RegisterSessionService service;
    private final SalesitemService salesitemService;;

    @PostMapping("/open")
    public RegisterSession openSession(
            @RequestBody Branch branch
    ) {
        return service.openSession(branch);
    }

  
@GetMapping("/active/{branchId}")
public RegisterSession getActiveSession(
        @PathVariable Long branchId
) {

    Branch branch =
            new Branch();

    branch.setBranchid(
            branchId
    );

    return service
            .getActiveSession(
                    branch
            )
            .orElseThrow(
                    () -> new RuntimeException(
                            "No active session"
                    )
            );
}
    @GetMapping("/all")
    public List<RegisterSession> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public RegisterSession getById(
            @PathVariable Long id
    ) {
        return service.getById(id)
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        service.delete(id);
    }
  
}