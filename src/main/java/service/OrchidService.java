package service;

import pojo.Orchid;
import java.util.List;
import java.util.Optional;

public interface OrchidService {
    List<Orchid> getAllOrchids();
    Optional<Orchid> getOrchidById(String id);
    Orchid createOrchid(Orchid orchid);
    Orchid updateOrchid(String id, Orchid orchid);
    void deleteOrchid(String id);
    List<Orchid> getOrchidsByCategory(Long categoryId);
    List<Orchid> searchOrchidsByName(String name);
}
