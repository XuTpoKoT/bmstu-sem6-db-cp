package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.model.Card;
import com.music_shop.DB.API.CardRepo;
import com.music_shop.DB.jdbc.mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CardRepoImpl implements CardRepo {
    private static final String SQL_UPDATE_CARD = """
        UPDATE public.card 
        SET bonuses = :bonuses
        WHERE user_login = :customer_login
    """;
    private static final String SQL_GET_CARD_BY_CUSTOMER_LOGIN = """
        SELECT *
        FROM public.card
        WHERE user_login = :customer_login 
    """;
    private static final String SQL_ADD_CARD = """
        INSERT INTO public.card (user_login, bonuses) 
        VALUES (:user_login, 0) 
    """;
    private static final String SQL_SET_ROLE_CUSTOMER = """
        SET ROLE customer;
    """;
    private static final String SQL_SET_ROLE_UNREGISTERED = """
        SET ROLE unregistered;
    """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CardMapper cardMapper;

    @Autowired
    public CardRepoImpl(NamedParameterJdbcTemplate jdbcTemplate, CardMapper cardMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.cardMapper = cardMapper;
    }

    @Override
    public Card getCardByCustomerLogin(String customerLogin) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("customer_login", customerLogin);

        Card card;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_CUSTOMER, new MapSqlParameterSource());
            card = jdbcTemplate.queryForObject(SQL_GET_CARD_BY_CUSTOMER_LOGIN, params, cardMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
        return card;
    }

    @Override
    public void createCard(String customerLogin) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_login", customerLogin);

        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            jdbcTemplate.update(SQL_ADD_CARD, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void updateCard(Card card) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("customer_login", card.getCustomerLogin());
        params.addValue("bonuses", card.getBonuses());

        try {
            jdbcTemplate.update(SQL_SET_ROLE_CUSTOMER, new MapSqlParameterSource());
            jdbcTemplate.update(SQL_UPDATE_CARD, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }
}
