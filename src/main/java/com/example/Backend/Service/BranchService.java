package com.example.Backend.Service;

import com.example.Backend.Entity.Branch;
import com.example.Backend.Repository.BranchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public Branch registerBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    public Optional<Branch> updateBranch(Long id, Branch branch) {
        return branchRepository.findById(id).map(existing -> {
            if (branch.getBranchname() != null) {
                existing.setBranchname(branch.getBranchname());
            }
            if (branch.getBranchtype() != null) {
                existing.setBranchtype(branch.getBranchtype());
            }
            if (branch.getAddress() != null) {
                existing.setAddress(branch.getAddress());
            }
            if (branch.getPhone() != null) {
                existing.setPhone(branch.getPhone());
            }
            if (branch.getStatus() != null) {
                existing.setStatus(branch.getStatus());
            }
            return branchRepository.save(existing);
        });
    }

    public boolean deleteBranch(Long id) {
        if (branchRepository.existsById(id)) {
            branchRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
