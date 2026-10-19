package cput.ac.za.ecommerce.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @Column(name = "reset_token_id", nullable = false, updatable = false, length = 50)
    private String resetTokenId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, updatable = false, length = 64)
    private String tokenHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private LocalDateTime expiresAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    protected PasswordResetToken() {
    }

    public PasswordResetToken(
            String resetTokenId,
            User user,
            String tokenHash,
            LocalDateTime createdAt,
            LocalDateTime expiresAt
    ) {
        if (resetTokenId == null || resetTokenId.isBlank()
                || user == null
                || tokenHash == null || tokenHash.isBlank()
                || createdAt == null
                || expiresAt == null
                || !expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("Password reset token details are invalid");
        }

        this.resetTokenId = resetTokenId;
        this.user = user;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public boolean isUsableAt(LocalDateTime timestamp) {
        return usedAt == null
                && timestamp != null
                && expiresAt.isAfter(timestamp);
    }

    public void markUsed(LocalDateTime timestamp) {
        if (usedAt == null) {
            usedAt = timestamp;
        }
    }
}
