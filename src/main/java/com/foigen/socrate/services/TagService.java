package com.foigen.socrate.services;

import com.foigen.socrate.entities.RateTag;
import com.foigen.socrate.repositories.RateTagRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
    private final RateTagRepository repository;

    public TagService(RateTagRepository repository) {
        this.repository = repository;
    }

    public Long getTagCount() {
        return repository.count();
    }

    public List<RateTag> findAllTags() {
        return repository.findAll();
    }
    public List<RateTag> findAllTags(String filter) {
        if(filter==null ||filter.isEmpty()){
            return repository.findAll();
        }
        return repository.search(filter);
    }

    public void delete(RateTag tag) {
        repository.delete(tag);
    }
    public  RateTag save(RateTag tag){
        if(repository.search(tag.getName()).isEmpty())
            return repository.save(tag);
        return tag;
    }
}

