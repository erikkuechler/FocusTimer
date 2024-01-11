package htwberlin.focustimer.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import htwberlin.focustimer.entity.Interval;

@Repository
public interface IntervalRepository extends CrudRepository<Interval, Long> { }
