package com.example.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.Backend.Dto.TenantRequest;
import com.example.Backend.Service.TenantProvisionService;
import com.example.Backend.Dto.TenantLoginRequest;
import com.example.Backend.Dto.TenantLoginResponse;

@RestController
@RequestMapping("/tenant")
public class TenantController {

    @Autowired
    private TenantProvisionService
            tenantProvisionService;

    @PostMapping("/create")
    public String createTenant(

            @RequestBody
            TenantRequest request
    ) {

        return tenantProvisionService
                .createTenant(request);
    }
    @PostMapping("/login")
public TenantLoginResponse login(

        @RequestBody
        TenantLoginRequest request
) {

    return tenantProvisionService.login(

            request.getUsername(),
            request.getPassword()
    );
}
}