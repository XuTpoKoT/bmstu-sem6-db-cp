package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.model.Card;
import com.music_shop.DB.API.CardRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "/scheme.sql")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class CardRepoTest {
    @Autowired
    private CardRepo cardRepository;

    @Test
    public void givenValidId_whenGetProductById_thenSuccess() {
        String login = "Bob";
        Card expectedCard = new Card(login, 500);

        Card card = cardRepository.getCardByCustomerLogin(login);

        Assertions.assertEquals(expectedCard.getBonuses(), card.getBonuses());
    }
}
