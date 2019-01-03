package net.fp.backBook.services;

import lombok.extern.slf4j.Slf4j;
import net.fp.backBook.exceptions.AddException;
import net.fp.backBook.exceptions.DeleteException;
import net.fp.backBook.exceptions.GetException;
import net.fp.backBook.exceptions.ModifyException;
import net.fp.backBook.model.Offer;
import net.fp.backBook.model.User;
import net.fp.backBook.repositories.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.regex;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.startsWith;

@Slf4j
@Service
public class OfferServiceImpl implements OfferService {

    private OfferRepository offerRepository;

    @Autowired
    public OfferServiceImpl(final OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    @Override
    public Offer getById(String id) {
        try {
            return offerRepository.findById(id).orElseThrow( () -> new GetException("Cannot find offer by id."));
        } catch (final Exception e) {
            log.error("Error during getting Offer object, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getAllOffers() {
        try {
            return offerRepository.findAll();
        } catch (final Exception e) {
            log.error("Error during getting Offer objects, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public void deleteOffer(String id) {
        try {
            offerRepository.deleteById(id);
        } catch (final Exception e) {
            log.error("Error during deleting Offer object by id, {}", e);
            throw new DeleteException("Error occurred", e);
        }
    }

    @Override
    public Offer addOffer(Offer offer) {
        try {
            offerRepository.insert(offer);
        } catch (final Exception e) {
            log.error("Error during inserting Offer object, {}", e);
            throw new AddException("Error occurred", e);
        }
        return offer;
    }

    @Override
    public Offer modifyOffer(Offer offer) {
        try {
            offerRepository.save(offer);
        } catch (final Exception e) {
            log.error("Error during saving Offer object, {}", e);
            throw new ModifyException("Error occurred", e);
        }
        return offer;
    }

    @Override
    public List<Offer> getAllByBookTitle(String bookTitle) {
        try {
            return offerRepository.findAllByBookTitle(bookTitle);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by title, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getAllByBookPublisher(String bookPublisher) {
        try {
            return offerRepository.findAllByBookPublisher(bookPublisher);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by publisher, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getAllBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return offerRepository.findAllByCreatedAtBetween(startDate, endDate);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects between dates, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getAllByOfferOwner(User user) {
        try {
            return offerRepository.findAllByOfferOwner(user);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by owner, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getAllByCity(String city) {
        try {
            return offerRepository.findAllByCity(city);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by city, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getAllByVoivodeship(String voivodeship) {
        try {
            return offerRepository.findAllByVoivodeship(voivodeship);
        } catch (final Exception e) {
            log.error("Error during getting Offer objects by voivodeship, {}", e);
            throw new GetException("Error occurred", e);
        }
    }

    @Override
    public List<Offer> getByFilter(Offer offer) {
        ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withMatcher("city", regex().ignoreCase())
                .withMatcher("voivodeship", regex().ignoreCase())
                .withMatcher("offerName", startsWith().ignoreCase())
                .withMatcher("bookTitle", regex().ignoreCase())
                .withMatcher("bookPublisher", regex().ignoreCase())
                .withMatcher("bookReleaseYear", startsWith());
        Example<Offer> offerExample = Example.of(offer, exampleMatcher);
        return offerRepository.findAll(offerExample);
    }
}