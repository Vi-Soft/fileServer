package com.visoft.file.service.service.folder;

import com.visoft.file.service.persistance.entity.Folder;
import com.visoft.file.service.service.abstractService.AbstractService;
import io.undertow.server.HttpServerExchange;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Interface for action for {@link Folder}
 */
public interface FolderService extends AbstractService<Folder> {

    /**
     * Create
     *
     * @param folder folder
     */
    void create(
            String folder,
            String mutualFolder,
            String projectName,
            String taskName
    );

    /**
     * Search by id. If folder not found send status code {@link com.visoft.file.service.service.ErrorConst#NOT_FOUND NOT_FOUND},
     * else send json from {@link com.visoft.file.service.dto.folder.FolderOutcomeDto OutcomeDto}
     *
     * @param exchange http exchange
     */
    void findById(HttpServerExchange exchange);

    /**
     * Search folder by id
     *
     * @param id id
     * @return true if exists, else false
     */
    boolean existsFolder(ObjectId id);

    /**
     * Delete folder by id. Delete it folder in users. Delete folder in fileSystem. Delete folder zip report.
     * <p>
     * If id equals null or not found  - send status code or not found in fileSystem {@link com.visoft.file.service.service.ErrorConst#NOT_FOUND NOT_FOUND}.
     * </p>
     *
     * @param httpServerExchange http exchange
     */
    void delete(HttpServerExchange httpServerExchange);

    /**
     * Send all folder
     *
     * @param exchange http exchange
     */
    void findAll(HttpServerExchange exchange);

    /**
     * find folder by name
     *
     * @param folder folder path
     */
    Folder findByFolder(String folder);

    /**
     * Convert {@link Folder#getId() id} to String
     *
     * @param folders list folder
     * @return null if folders null or folders is empty.Else return list ids by folders
     */
    List<String> getIdsFromObjectId(List<ObjectId> folders);

    /**
     * Convert {@link Folder#getId() id} to String
     *
     * @param ids list ids
     * @return null if folders null or folders is empty or folder in folders not found.Else return list ids by folders
     */
    List<ObjectId> getIdsFromStrings(List<String> ids);
}