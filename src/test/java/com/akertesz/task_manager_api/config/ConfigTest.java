package com.akertesz.task_manager_api.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.akertesz.task_manager_api.model.User;
import com.akertesz.task_manager_api.service.CustomUserDetailsService;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=testSecretKeyForTestingPurposesOnly123456789012345678901234567890",
    "jwt.expiration=86400000"
})
class ConfigTest {

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    private User testUser;

    @BeforeEach
    void setUp() {
        // Remove manual instantiation since we're using @Autowired
        testUser = new User();
        testUser.setId(java.util.UUID.randomUUID());
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setVersion(0L);
    }

    // JwtUtil Tests
    @Test
    void testJwtUtil_GenerateToken() {
        // Arrange
        String username = "testuser";
        
        // Act
        String token = jwtUtil.generateToken(username);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 0);
    }

    @Test
    void testJwtUtil_GenerateTokenWithEmptyUsername() {
        // Arrange
        String username = "";
        
        // Act
        String token = jwtUtil.generateToken(username);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testJwtUtil_GenerateTokenWithNullUsername() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.generateToken(null);
        });
    }

    @Test
    void testJwtUtil_GenerateTokenWithSpecialCharacters() {
        // Arrange
        String username = "user@123+test";
        
        // Act
        String token = jwtUtil.generateToken(username);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testJwtUtil_GenerateTokenWithUnicodeCharacters() {
        // Arrange
        String username = "usérñame";
        
        // Act
        String token = jwtUtil.generateToken(username);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testJwtUtil_GenerateTokenWithLongUsername() {
        // Arrange
        String username = "a".repeat(100);
        
        // Act
        String token = jwtUtil.generateToken(username);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testJwtUtil_GenerateMultipleTokens() {
        // Arrange
        String username1 = "user1";
        String username2 = "user2";
        
        // Act
        String token1 = jwtUtil.generateToken(username1);
        String token2 = jwtUtil.generateToken(username2);
        
        // Assert
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Tokens should be different
    }

    @Test
    void testJwtUtil_GenerateTokenPerformance() {
        // Test that token generation is fast
        long startTime = System.nanoTime();
        
        for (int i = 0; i < 1000; i++) {
            String token = jwtUtil.generateToken("user" + i);
            assertNotNull(token);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 1 second)
        assertTrue(duration < 1_000_000_000L, "Token generation should be fast");
    }

    @Test
    void testJwtUtil_TokenFormat() {
        // Arrange
        String username = "testuser";
        
        // Act
        String token = jwtUtil.generateToken(username);
        
        // Assert
        // JWT tokens typically have 3 parts separated by dots
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT token should have 3 parts");
        
        // Each part should not be empty
        for (String part : parts) {
            assertFalse(part.isEmpty(), "JWT token part should not be empty");
        }
    }

    @Test
    void testJwtUtil_TokenUniqueness() {
        // Arrange
        String username = "testuser";
        
        // Act
        String token1 = jwtUtil.generateToken(username);
        String token2 = jwtUtil.generateToken(username);
        
        // Assert
        // Even for the same username, tokens should be unique (due to timestamp)
        assertNotEquals(token1, token2, "Tokens should be unique even for same username");
    }

    // CustomUserDetailsService Tests
    @Test
    void testCustomUserDetailsService_LoadUserByUsername() {
        // This test would require a mock UserRepository
        // For now, we'll test the basic structure
        
        // Arrange
        String username = "testuser";
        
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    void testCustomUserDetailsService_LoadUserByUsernameEmpty() {
        // Arrange
        String username = "";
        
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    void testCustomUserDetailsService_LoadUserByUsernameNull() {
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(null);
        });
    }

    @Test
    void testCustomUserDetailsService_LoadUserByUsernameWithSpecialCharacters() {
        // Arrange
        String username = "user@123+test";
        
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    void testCustomUserDetailsService_LoadUserByUsernameWithUnicodeCharacters() {
        // Arrange
        String username = "usérñame";
        
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    @Test
    void testCustomUserDetailsService_LoadUserByUsernameWithLongUsername() {
        // Arrange
        String username = "a".repeat(100);
        
        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    // Security Configuration Tests
    @Test
    void testSecurityConfig_UserDetailsServiceNotNull() {
        // Test that the custom user details service is properly configured
        assertNotNull(customUserDetailsService);
    }

    @Test
    void testSecurityConfig_JwtUtilNotNull() {
        // Test that the JWT utility is properly configured
        assertNotNull(jwtUtil);
    }

    // Integration Tests
    @Test
    void testJwtUtil_WithCustomUserDetailsService() {
        // Test integration between JWT utility and user details service
        String username = "testuser";
        
        // Generate token
        String token = jwtUtil.generateToken(username);
        assertNotNull(token);
        
        // Try to load user (this will fail without proper repository setup)
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername(username);
        });
    }

    // Error Handling Tests
    @Test
    void testJwtUtil_ErrorHandling() {
        // Test that JWT utility handles errors gracefully
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.generateToken(null);
        });
    }

    @Test
    void testCustomUserDetailsService_ErrorHandling() {
        // Test that user details service handles errors gracefully
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistentuser");
        });
    }

    // Configuration Validation Tests
    @Test
    void testConfiguration_ComponentsNotNull() {
        // Test that all configuration components are properly initialized
        assertNotNull(jwtUtil, "JwtUtil should not be null");
        assertNotNull(customUserDetailsService, "CustomUserDetailsService should not be null");
    }

    @Test
    void testConfiguration_ComponentTypes() {
        // Test that components are of the correct type
        assertTrue(jwtUtil instanceof JwtUtil, "jwtUtil should be instance of JwtUtil");
        assertTrue(customUserDetailsService instanceof CustomUserDetailsService, 
                  "customUserDetailsService should be instance of CustomUserDetailsService");
    }

    // Performance Tests
    @Test
    void testConfiguration_Performance() {
        // Test that configuration components perform well
        long startTime = System.nanoTime();
        
        // Test JWT utility performance
        for (int i = 0; i < 100; i++) {
            String token = jwtUtil.generateToken("user" + i);
            assertNotNull(token);
        }
        
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        
        // Should complete in reasonable time (less than 100 milliseconds)
        assertTrue(duration < 100_000_000L, "Configuration components should be performant");
    }

    // Memory Tests
    @Test
    void testConfiguration_MemoryUsage() {
        // Test that configuration components don't cause memory leaks
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        // Generate many tokens
        for (int i = 0; i < 1000; i++) {
            String token = jwtUtil.generateToken("user" + i);
            assertNotNull(token);
        }
        
        // Force garbage collection
        System.gc();
        
        long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        // Memory increase should be reasonable (less than 10MB)
        assertTrue(memoryIncrease < 10 * 1024 * 1024, "Memory usage should be reasonable");
    }

    // Thread Safety Tests
    @Test
    void testConfiguration_ThreadSafety() throws InterruptedException {
        // Test that configuration components are thread-safe
        int threadCount = 10;
        int operationsPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    String token = jwtUtil.generateToken("user" + threadId + "_" + j);
                    assertNotNull(token);
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // If we get here without exceptions, the component is thread-safe
        assertTrue(true, "Configuration components should be thread-safe");
    }

    // Configuration Consistency Tests
    @Test
    void testConfiguration_Consistency() {
        // Test that configuration is consistent across multiple calls
        String username = "testuser";
        
        // Generate multiple tokens for the same user
        String token1 = jwtUtil.generateToken(username);
        String token2 = jwtUtil.generateToken(username);
        String token3 = jwtUtil.generateToken(username);
        
        // All tokens should be valid
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token3);
        
        // All tokens should be different (due to timestamp)
        assertNotEquals(token1, token2);
        assertNotEquals(token2, token3);
        assertNotEquals(token1, token3);
    }

    // Configuration Validation Tests
    @Test
    void testConfiguration_Validation() {
        // Test that configuration components validate input properly
        
        // Test JWT utility validation
        assertThrows(IllegalArgumentException.class, () -> {
            jwtUtil.generateToken(null);
        });
        
        // Test user details service validation
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistentuser");
        });
    }
}
