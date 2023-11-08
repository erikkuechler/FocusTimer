package htwberlin.focustimer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import htwberlin.focustimer.entity.Thing;

@Repository
public interface ThingRepository extends CrudRepository<Thing, Long> { }