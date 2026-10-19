package cput.ac.za.ecommerce.controller;

import cput.ac.za.ecommerce.response.AdminDashboardResponse;
import cput.ac.za.ecommerce.service.IAdminDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMINISTRATOR')")
public class AdminDashboardController {

    private final IAdminDashboardService dashboardService;

    public AdminDashboardController(
            IAdminDashboardService dashboardService
    ) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<AdminDashboardResponse> getSummary() {
        return ResponseEntity.ok(
                dashboardService.getSummary()
        );
    }
}
