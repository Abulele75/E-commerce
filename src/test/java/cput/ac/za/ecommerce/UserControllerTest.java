package cput.ac.za.ecommerce;

import cput.ac.za.ecommerce.controller.UserManagementController;
import cput.ac.za.ecommerce.domain.AccountProfile;
import cput.ac.za.ecommerce.domain.Customer;
import cput.ac.za.ecommerce.factory.UserManagementFactory;
import cput.ac.za.ecommerce.service.IUserManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserManagementController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserManagementService userService;

    @Test
    void createCustomerShouldReturnCreatedCustomer() throws Exception {
        UserManagementController.CreateCustomerRequest request =
                new UserManagementController.CreateCustomerRequest(
                        "Ava",
                        "Mokoena",
                        "ava@example.com",
                        "+27110000000",
                        "CUST-001"
                );
        AccountProfile profile = createProfile("Ava", "Mokoena", "ava@example.com");
        Customer customer = UserManagementFactory.createCustomer(profile, "CUST-001");

        when(userService.registerCustomer(any(AccountProfile.class), eq("CUST-001"))).thenReturn(customer);

        mockMvc.perform(post("/api/users/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customer.getUserId()))
                .andExpect(jsonPath("$.customerNumber").value("CUST-001"))
                .andExpect(jsonPath("$.accountProfile.email").value("ava@example.com"));
    }

    @Test
    void getUserShouldReturnUserById() throws Exception {
        Customer customer = UserManagementFactory.createCustomer(
                createProfile("Ava", "Mokoena", "ava@example.com"),
                "CUST-001"
        );

        when(userService.getUser(customer.getUserId())).thenReturn(customer);

        mockMvc.perform(get("/api/users/{userId}", customer.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customer.getUserId()))
                .andExpect(jsonPath("$.customerNumber").value("CUST-001"));
    }

    @Test
    void getUsersShouldReturnAllUsers() throws Exception {
        Customer customer = UserManagementFactory.createCustomer(
                createProfile("Ava", "Mokoena", "ava@example.com"),
                "CUST-001"
        );

        when(userService.listUsers()).thenReturn(List.of(customer));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(customer.getUserId()))
                .andExpect(jsonPath("$[0].customerNumber").value("CUST-001"));
    }

    @Test
    void updateProfileShouldReturnUpdatedUser() throws Exception {
        AccountProfile updatedProfile = createProfile("Ava", "Mokoena", "ava.new@example.com");
        Customer customer = UserManagementFactory.createCustomer(updatedProfile, "CUST-001");

        when(userService.updateProfile(eq(customer.getUserId()), any(AccountProfile.class))).thenReturn(customer);

        mockMvc.perform(put("/api/users/{userId}/profile", customer.getUserId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountProfile.email").value("ava.new@example.com"));
    }

    @Test
    void deleteUserShouldCallService() throws Exception {
        doNothing().when(userService).removeUser("USR-001");

        mockMvc.perform(delete("/api/users/{userId}", "USR-001"))
                .andExpect(status().isOk());

        verify(userService).removeUser("USR-001");
    }

    private AccountProfile createProfile(String firstName, String lastName, String email) {
        return AccountProfile.builder()
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .setPhoneNumber("+27110000000")
                .build();
    }
}
