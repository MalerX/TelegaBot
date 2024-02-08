package com.malerx.bot.storage;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDatabase;
import com.arangodb.entity.BaseDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malerx.utils.ResourceUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class AbstractCache<T> {
    private static final String BODY = "body";
    private static final String LABEL = "label";
    private static final String CACHE = "document_cache";
    private static final String COLLECTION = "@collection";
    private static final String SEARCH_AQL = "aql/search_in_cache.aql";
    protected final ArangoDatabase database;
    protected final ArangoCollection cache;
    protected final ObjectMapper mapper;
    protected final Class<T> tClass;

    public AbstractCache(ArangoDatabase database, ObjectMapper mapper, Class<T> tClass) {
        this.database = database;
        this.mapper = mapper;
        this.tClass = tClass;
        this.cache = database.collection(CACHE);
    }

    public void saveDocument(T body, String label) {
        log.debug("saveDocument() -> save document for caching");
        BaseDocument document = createDocument(body, label);
        cache.insertDocument(document);
    }

    public Optional<T> searchDocument(String label) {
        log.debug("searchDocument() -> search cached document in {}", CACHE);
        String aql = ResourceUtil.readFile(SEARCH_AQL);
        if (aql == null || aql.isEmpty()) {
            log.error("searchDocument() -> not found search aql");
            return Optional.empty();
        }
        Map<String, Object> bindVars = Map.of(COLLECTION, CACHE, LABEL, label);
        try (ArangoCursor<BaseDocument> cursor = database.query(aql, BaseDocument.class, bindVars)) {
            List<BaseDocument> documents = cursor.asListRemaining();
            if (documents.isEmpty())
                return Optional.empty();
            log.debug("searchDocument() -> record was found in the database");
            BaseDocument document = documents.getFirst();
            T body = mapper.convertValue(document.getAttribute(BODY), tClass);
            return Optional.of(body);
        } catch (Exception e) {
            log.error("error: ", e);
            return Optional.empty();
        }
    }

    protected BaseDocument createDocument(T body, String label) {
        log.debug("createDocument() -> create wrapper document for saving");
        BaseDocument document = new BaseDocument(UUID.randomUUID().toString());
        document.addAttribute(LABEL, label);
        document.addAttribute(BODY, body);
        document.addAttribute("date", LocalDateTime.now().toString());
        return document;
    }
}
