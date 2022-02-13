package technology.touchmars.edu.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import technology.touchmars.edu.web.rest.TestUtil;

class UserProfileInterestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfileInterest.class);
        UserProfileInterest userProfileInterest1 = new UserProfileInterest();
        userProfileInterest1.setId(1L);
        UserProfileInterest userProfileInterest2 = new UserProfileInterest();
        userProfileInterest2.setId(userProfileInterest1.getId());
        assertThat(userProfileInterest1).isEqualTo(userProfileInterest2);
        userProfileInterest2.setId(2L);
        assertThat(userProfileInterest1).isNotEqualTo(userProfileInterest2);
        userProfileInterest1.setId(null);
        assertThat(userProfileInterest1).isNotEqualTo(userProfileInterest2);
    }
}
