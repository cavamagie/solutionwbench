/**
 * Repository for persisting and querying gift card aggregate roots in MongoDB.
 *
 * This repository provides Spring Data MongoDB access for the Giftcard aggregate root
 * and exposes query methods required by the domain for retrieving gift cards by customer.
 */
package k5.giftcard.domain.giftcard.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import k5.giftcard.domain.giftcard.entity.Giftcard;

/**
 * Spring Data MongoDB repository for Giftcard aggregate persistence operations.
 */
@Repository
public interface GiftcardRepository extends MongoRepository<Giftcard, String> {

    /**
     * Finds all gift cards belonging to the provided customer identifier.
     *
     * @param customerId the customer identifier used to filter gift cards
     * @return the list of gift cards owned by the customer
     */
    List<Giftcard> findByCustomerId(String customerId);
}

// Made with Bob
