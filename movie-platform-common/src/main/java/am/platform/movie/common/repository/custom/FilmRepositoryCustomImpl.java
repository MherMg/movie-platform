package am.platform.movie.common.repository.custom;

import am.platform.movie.common.model.Film;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * @author mher13.02.94@gmail.com
 */

public class FilmRepositoryCustomImpl implements FilmRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public FilmRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Film> filter(String categoryId, LocalDate start, LocalDate end, int page, int size) {
        if (size == 0) {
            size = 10;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("created")));

        Query query = new Query();

        if (categoryId != null) {
            query.addCriteria(Criteria.where("categoryId").is(categoryId));
        }
        if (start != null) {
            query.addCriteria(Criteria.where("issueDate").gte(start).lt(end));
        }

        query.with(pageable);
        List<Film> powerBanks = mongoTemplate.find(query, Film.class);
        long count = mongoTemplate.count(query.skip(-1).limit(-1), Film.class);

        return new PageImpl<Film>(powerBanks, pageable, count);
    }

}
