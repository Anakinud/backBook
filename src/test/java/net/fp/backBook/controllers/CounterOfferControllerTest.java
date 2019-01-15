package net.fp.backBook.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.fp.backBook.configuration.RestResponseExceptionHandler;
import net.fp.backBook.dtos.CounterOfferDto;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.CounterOffer;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.Role;
import net.fp.backBook.model.User;
import net.fp.backBook.services.CounterOfferService;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = { EmbeddedMongoAutoConfiguration.class})
public class CounterOfferControllerTest {

    private MockMvc mockMvc;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Autowired
    private RestResponseExceptionHandler restResponseExceptionHandler;

    @Autowired
    private ModelMapper modelMapper;

    @Mock
    private ModelMapper modelMapperMock;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private CounterOfferService counterOfferService;

    @InjectMocks
    private CounterOfferController counterOfferController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(counterOfferController)
                .setControllerAdvice(restResponseExceptionHandler).build();
    }

    @Test
    public void addCounterOfferSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.add(counterOffer)).thenReturn(counterOffer);
        when(this.modelMapperMock.map(any(CounterOfferDto.class), eq(CounterOffer.class))).thenReturn(counterOffer);
        when(this.modelMapperMock.map(any(CounterOffer.class), eq(CounterOfferDto.class))).thenReturn(counterOfferDto);

        this.mockMvc.perform(
                post("/counterOffers")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andReturn();
    }

    @Test
    public void addCounterOfferFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.modelMapperMock.map(any(CounterOfferDto.class), eq(CounterOffer.class))).thenReturn(counterOffer);
        when(this.counterOfferService.add(counterOffer)).thenThrow(AddException.class);

        this.mockMvc.perform(
                post("/counterOffers")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }

    @Test
    public void modifyCounterOfferSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.modify(counterOffer)).thenReturn(counterOffer);
        when(this.modelMapperMock.map(any(CounterOfferDto.class), eq(CounterOffer.class))).thenReturn(counterOffer);
        when(this.modelMapperMock.map(any(CounterOffer.class), eq(CounterOfferDto.class))).thenReturn(counterOfferDto);

        this.mockMvc.perform(
                put("/counterOffers/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andReturn();
    }

    @Test
    public void modifyCounterOfferWrongIdFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        this.mockMvc.perform(
                put("/counterOffers/2")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }

    @Test
    public void modifyCounterOfferDatabaseExceptionFailure() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.modify(counterOffer)).thenThrow(ModifyException.class);
        when(this.modelMapperMock.map(any(CounterOfferDto.class), eq(CounterOffer.class))).thenReturn(counterOffer);

        this.mockMvc.perform(
                put("/counterOffers/1")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(counterOfferDto)))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }

    @Test
    public void getAllCounterOffersSuccess() throws Exception {

        User user1 = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser1 = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer1 = Offer.builder()
                .id("1")
                .bookTitle("title1")
                .bookReleaseYear("2018")
                .bookPublisher("publisher1")
                .offerName("offerName1")
                .offerOwner(offerUser1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city1")
                .voivodeship("voivodeship1")
                .fileId("1")
                .build();

        CounterOffer counterOffer1 = CounterOffer.builder()
                .id("1")
                .offer(offer1)
                .user(user1)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        User user2 = User.builder()
                .id("3")
                .login("login3")
                .password("password3")
                .email("email3")
                .name("name3")
                .lastName("lastName3")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser2 = User.builder()
                .id("4")
                .login("login4")
                .password("password4")
                .email("email4")
                .name("name4")
                .lastName("lastName4")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer2 = Offer.builder()
                .id("2")
                .bookTitle("title2")
                .bookReleaseYear("2018")
                .bookPublisher("publisher2")
                .offerName("offerName2")
                .offerOwner(offerUser2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city2")
                .voivodeship("voivodeship2")
                .fileId("2")
                .build();

        CounterOffer counterOffer2 = CounterOffer.builder()
                .id("2")
                .offer(offer2)
                .user(user2)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto1 = this.modelMapper.map(counterOffer1, CounterOfferDto.class);
        CounterOfferDto counterOfferDto2 = this.modelMapper.map(counterOffer2, CounterOfferDto.class);

        when(this.counterOfferService.getAll()).thenReturn(Lists.list(counterOffer1, counterOffer2));
        when(this.modelMapperMock.map(counterOffer1, CounterOfferDto.class)).thenReturn(counterOfferDto1);
        when(this.modelMapperMock.map(counterOffer2, CounterOfferDto.class)).thenReturn(counterOfferDto2);

        this.mockMvc.perform(
                get("/counterOffers"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].user.id").value("1"))
                .andExpect(jsonPath("$[0].user.name").value("name1"))
                .andExpect(jsonPath("$[0].user.lastName").value("lastName1"))
                .andExpect(jsonPath("$[0].user.email").value("email1"))
                .andExpect(jsonPath("$[0].offer.id").value("1"))
                .andExpect(jsonPath("$[0].offer.bookTitle").value("title1"))
                .andExpect(jsonPath("$[0].offer.bookPublisher").value("publisher1"))
                .andExpect(jsonPath("$[0].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[0].offer.active").value("true"))
                .andExpect(jsonPath("$[0].offer.city").value("city1"))
                .andExpect(jsonPath("$[0].offer.voivodeship").value("voivodeship1"))
                .andExpect(jsonPath("$[0].offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$[0].offer.offerOwner.email").value("email2"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].user.id").value("3"))
                .andExpect(jsonPath("$[1].user.name").value("name3"))
                .andExpect(jsonPath("$[1].user.lastName").value("lastName3"))
                .andExpect(jsonPath("$[1].user.email").value("email3"))
                .andExpect(jsonPath("$[1].offer.id").value("2"))
                .andExpect(jsonPath("$[1].offer.bookTitle").value("title2"))
                .andExpect(jsonPath("$[1].offer.bookPublisher").value("publisher2"))
                .andExpect(jsonPath("$[1].offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$[1].offer.active").value("true"))
                .andExpect(jsonPath("$[1].offer.city").value("city2"))
                .andExpect(jsonPath("$[1].offer.voivodeship").value("voivodeship2"))
                .andExpect(jsonPath("$[1].offer.offerOwner.id").value("4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.name").value("name4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.lastName").value("lastName4"))
                .andExpect(jsonPath("$[1].offer.offerOwner.email").value("email4"))
                .andReturn();
    }

    @Test
    public void getAllCounterOffersFailure() throws Exception {

        when(this.counterOfferService.getAll()).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/counterOffers"))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }

    @Test
    public void getCounterOfferByIdSuccess() throws Exception {

        User user = User.builder()
                .id("1")
                .login("login1")
                .password("password1")
                .email("email1")
                .name("name1")
                .lastName("lastName1")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        User offerUser = User.builder()
                .id("2")
                .login("login2")
                .password("password2")
                .email("email2")
                .name("name2")
                .lastName("lastName2")
                .roles(Collections.singletonList(new Role("1", "role"))).build();

        Offer offer = Offer.builder()
                .id("1")
                .bookTitle("title")
                .bookReleaseYear("2018")
                .bookPublisher("publisher")
                .offerName("offerName")
                .offerOwner(offerUser)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter))
                .active(true)
                .city("city")
                .voivodeship("voivodeship")
                .fileId("1")
                .build();

        CounterOffer counterOffer = CounterOffer.builder()
                .id("1")
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.parse("01-01-2018 12:00:00", dateTimeFormatter))
                .expires(LocalDateTime.parse("02-02-2018 12:00:00", dateTimeFormatter)).build();

        CounterOfferDto counterOfferDto = this.modelMapper.map(counterOffer, CounterOfferDto.class);

        when(this.counterOfferService.getById(counterOffer.getId())).thenReturn(counterOffer);
        when(this.modelMapperMock.map(counterOffer, CounterOfferDto.class)).thenReturn(counterOfferDto);

        this.mockMvc.perform(
                get("/counterOffers/" + counterOffer.getId()))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.user.id").value("1"))
                .andExpect(jsonPath("$.user.name").value("name1"))
                .andExpect(jsonPath("$.user.lastName").value("lastName1"))
                .andExpect(jsonPath("$.user.email").value("email1"))
                .andExpect(jsonPath("$.offer.id").value("1"))
                .andExpect(jsonPath("$.offer.bookTitle").value("title"))
                .andExpect(jsonPath("$.offer.bookPublisher").value("publisher"))
                .andExpect(jsonPath("$.offer.createdAt").value("01-01-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.expires").value("02-02-2018 12:00:00"))
                .andExpect(jsonPath("$.offer.active").value("true"))
                .andExpect(jsonPath("$.offer.city").value("city"))
                .andExpect(jsonPath("$.offer.voivodeship").value("voivodeship"))
                .andExpect(jsonPath("$.offer.offerOwner.id").value("2"))
                .andExpect(jsonPath("$.offer.offerOwner.name").value("name2"))
                .andExpect(jsonPath("$.offer.offerOwner.lastName").value("lastName2"))
                .andExpect(jsonPath("$.offer.offerOwner.email").value("email2"))
                .andReturn();
    }

    @Test
    public void getCounterOfferByIdFailure() throws Exception {

        String id = "1";

        when(this.counterOfferService.getById(id)).thenThrow(GetException.class);

        this.mockMvc.perform(
                get("/counterOffers/" + id))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andReturn();
    }
}
