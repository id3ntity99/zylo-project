package com.example.driveservice.fs;

import com.example.driveservice.dao.mongo.DriveRepository;
import com.example.driveservice.dao.redis.VfsCacheRepository;
import com.example.driveservice.document.Directory;
import com.example.driveservice.document.Node;
import com.example.driveservice.exception.VfsDeserializationException;
import com.example.driveservice.service.UploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
@RequiredArgsConstructor
public class VirtualFileSystemFactory {

  private final DriveRepository driveRepo;
  private final VfsCacheRepository cacheRepo;
  private final UploadService uploadService;
  private final ObjectMapper mapper;

  @Value("${zylo.plan.free.max-drive-size}")
  private DataSize freeDriveMaxSize;

  @Value("${zylo.plan.plus.max-drive-size}")
  private DataSize plusDriveMaxSize;

  private VirtualFileSystem createNewVfs(Directory entryNode, List<Node> nodes, String username,
      String subscription) {
    long maxSize = 0;
    if (subscription.equalsIgnoreCase("free")) {
      maxSize = freeDriveMaxSize.toBytes();
    } else if (subscription.equalsIgnoreCase("plus")) {
      maxSize = plusDriveMaxSize.toBytes();
    }

    return InMemoryFileSystem.builder()
        .nodes(nodes)
        .root(entryNode)
        .username(username)
        .subscription(subscription)
        .currentSize(0)
        .maxSize(maxSize)
        .driveRepo(driveRepo)
        .cacheRepo(cacheRepo)
        .uploadService(uploadService)
        .build();
  }

  private VirtualFileSystem getCachedVfs(String username) throws VfsDeserializationException {
    String serializedVfs = cacheRepo.get(username);

    if (serializedVfs == null) {
      return null;
    }

    try {
      return mapper.readValue(serializedVfs, InMemoryFileSystem.class);
    } catch (JsonProcessingException e) {
      String message = "VFS 캐시를 역직렬화 하는 도중 예외가 발생했습니다.";
      throw new VfsDeserializationException(message, e.getCause());
    }
  }

  public VirtualFileSystem create(Directory entry, List<Node> nodes, String username,
      String subscription)
      throws VfsDeserializationException {
    VirtualFileSystem cachedVfs = getCachedVfs(username);

    if (cachedVfs != null) {
      return cachedVfs;
    }

    return createNewVfs(entry, nodes, username, subscription);
  }

}
