package com.example.Backend.Service;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Entity.RegisterSession;
import com.example.Backend.Repository.RegisterSessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RegisterSessionService {

    private final RegisterSessionRepository repository;

    // OPEN SESSION
    public RegisterSession openSession(
            Branch branch
    ) {

        Optional<RegisterSession> activeSession =
                repository.findByBranchAndActiveTrue(
                        branch
                );

        if (activeSession.isPresent()) {
            return activeSession.get();
        }

        RegisterSession session =
                new RegisterSession();

        session.setBranch(branch);
        session.setOpenedat(
                LocalDateTime.now()
        );
        session.setActive(true);
        session.setTotalSales(0.0);
        session.setTotalBills(0);

        return repository.save(session);
    }

    // CLOSE SESSION
    public RegisterSession closeSession(
            Long sessionId,
            Double totalSales,
            Integer totalBills
    ) {

        RegisterSession session =
                repository.findById(sessionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Session not found"
                                )
                        );

        // Prevent double close
        if (!session.getActive()) {

            throw new RuntimeException(
                    "Register already closed"
            );
        }

        session.setClosedat(
                LocalDateTime.now()
        );

        session.setActive(false);

        session.setTotalSales(
                totalSales
        );

        session.setTotalBills(
                totalBills
        );

        RegisterSession closedSession =
                repository.save(session);

        // AUTO OPEN NEW SESSION
        RegisterSession newSession =
                new RegisterSession();

        newSession.setBranch(
                session.getBranch()
        );

        newSession.setOpenedat(
                LocalDateTime.now()
        );

        newSession.setActive(true);

        newSession.setTotalSales(0.0);

        newSession.setTotalBills(0);

        repository.save(newSession);

        return closedSession;
    }

    // GET ACTIVE SESSION
    public Optional<RegisterSession> getActiveSession(
            Branch branch
    ) {

        return repository
                .findByBranchAndActiveTrue(
                        branch
                );
    }

    // GET ALL
    public List<RegisterSession> getAll() {

        return repository.findAll();
    }

    // GET BY ID
    public Optional<RegisterSession> getById(
            Long id
    ) {

        return repository.findById(id);
    }

    // DELETE
    public void delete(
            Long id
    ) {

        repository.deleteById(id);
    }
}