package com.incident.tracker.incident.infrastructure.persistence.repository;

import com.incident.tracker.incident.domain.model.Incident;
import com.incident.tracker.incident.domain.model.IncidentStatus;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentEntity;
import com.incident.tracker.incident.domain.port.IncidentRepositoryPort;
import com.incident.tracker.incident.infrastructure.persistence.entity.IncidentStatusEntity;
import com.incident.tracker.incident.infrastructure.persistence.mapper.IncidentPersistenceMapper;
import com.incident.tracker.shared.domain.EnumFinderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class IncidentRepositoryAdapter implements IncidentRepositoryPort {
    private final IncidentJpaRepository incidentJpaRepository;
    private final IncidentPersistenceMapper incidentPersistenceMapper;


    public IncidentRepositoryAdapter(IncidentJpaRepository incidentRepository, IncidentPersistenceMapper incidentPersistenceMapper) {
        this.incidentJpaRepository = incidentRepository;
        this.incidentPersistenceMapper = incidentPersistenceMapper;
    }

    @Override
    public Incident save(Incident incident) {
        IncidentEntity entity = incidentPersistenceMapper.toEntity(incident);
        IncidentEntity saved = incidentJpaRepository.save(entity);
        return incidentPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Incident> findById(Long id) {
        Optional<IncidentEntity> entity = incidentJpaRepository.findById(id);
        return entity.map(incidentPersistenceMapper::toDomain);
                
    }

    @Override
    public List<Incident> findAll() {
        return incidentJpaRepository.findAll()
                .stream()
                .map(incidentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Incident> findByIncidentStatus(IncidentStatus status) {
        IncidentStatusEntity incidentStatusEntity = EnumFinderUtils.parseByName(IncidentStatusEntity.class, status.name());
        return incidentJpaRepository.findByIncidentStatusEntity(incidentStatusEntity)
                .stream()
                .map(incidentPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAll() {
         incidentJpaRepository.deleteAll();
    }
}
