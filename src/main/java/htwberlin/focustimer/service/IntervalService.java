package htwberlin.focustimer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import htwberlin.focustimer.entity.Interval;
import htwberlin.focustimer.repository.IntervalRepository;

@Service
public class IntervalService {

    @Autowired
    IntervalRepository repo;

    public Interval save(Interval interval) {
        return repo.save(interval);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Interval get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException());
    }

    public List<Interval> getAll() {
        Iterable<Interval> iterator = repo.findAll();
        List<Interval> intervals = new ArrayList<Interval>();
        for (Interval interval : iterator)  intervals.add(interval);
        return intervals;
    }
}
