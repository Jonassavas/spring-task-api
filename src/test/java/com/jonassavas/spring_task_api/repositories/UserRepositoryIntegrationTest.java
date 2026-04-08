package com.jonassavas.spring_task_api.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jonassavas.spring_task_api.domain.entities.UserEntity;
import com.jonassavas.util.TestUserData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired private UserRepository underTest;

    // Optional<UserEntity> findByUsername(String username);
    @Test
    public void testFindByUsernameReturnsUserWhenExists() {
        UserEntity user = underTest.saveAndFlush(TestUserData.createTestUserEntityA());

        var result = underTest.findByUsername(user.getUsername());

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void testFindByUsernameReturnsEmptyWhenNotFound() {
        var result = underTest.findByUsername("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    public void testUsernameMustBeUnique() {
        UserEntity user1 = TestUserData.createTestUserEntityA();
        UserEntity user2 = TestUserData.createTestUserEntityA(); // same username

        underTest.saveAndFlush(user1);

        assertThatThrownBy(() -> underTest.saveAndFlush(user2)).isInstanceOf(Exception.class);
    }

    // boolean existsByUsername(String username);
    @Test
    public void testExistsByUsernameReturnsTrueWhenExists() {
        UserEntity user = underTest.saveAndFlush(TestUserData.createTestUserEntityA());

        boolean exists = underTest.existsByUsername(user.getUsername());

        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByUsernameReturnsFalseWhenNotExists() {
        boolean exists = underTest.existsByUsername("nonexistent");

        assertThat(exists).isFalse();
    }

    // boolean existsByEmail(String email);
    @Test
    public void testExistsByEmailReturnsTrueWhenExists() {
        UserEntity user = underTest.saveAndFlush(TestUserData.createTestUserEntityA());

        boolean exists = underTest.existsByEmail(user.getEmail());

        assertThat(exists).isTrue();
    }

    @Test
    public void testExistsByEmailReturnsFalseWhenNotExists() {
        boolean exists = underTest.existsByEmail("fake@email.com");

        assertThat(exists).isFalse();
    }

    // TODO Implement case-insensitive usernames later
    @Test
    public void testFindByUsernameIsCaseSensitive() {
        UserEntity user = underTest.saveAndFlush(TestUserData.createTestUserEntityA());

        var result = underTest.findByUsername(user.getUsername().toUpperCase());

        assertThat(result).isEmpty();
    }
}
