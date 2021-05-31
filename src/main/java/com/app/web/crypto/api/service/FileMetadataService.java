package com.app.web.crypto.api.service;

import com.app.web.crypto.api.model.CommentDTO;
import com.app.web.crypto.api.model.FileMetadata;
import com.app.web.crypto.api.model.FileMetadataDTO;
import com.app.web.crypto.api.model.User;
import com.app.web.crypto.api.repository.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FileMetadataService {

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private CommentService commentService;

    public List<FileMetadataDTO> getAllWithRestrictDownload(Long userId) {

        List<FileMetadataDTO> fileMetadataDTOList = new ArrayList<>();
        List<FileMetadata> fileMetadataList = fileMetadataRepository.findAll();

        if (fileMetadataList == null) {
            return fileMetadataDTOList;
        }

        for (FileMetadata fm: fileMetadataList) {
            FileMetadataDTO fmDTO = new FileMetadataDTO();
            fmDTO.setId(fm.getId());
            fmDTO.setFilename(fm.getFilename());
            fmDTO.setSenderUsername(fm.getSenderUsername());
            boolean isDownloadable = fm.getReceivers().contains(new User(userId));
            fmDTO.setDownloadable(isDownloadable);

            List<CommentDTO> comments = commentService.findByFileMetadataId(fm.getId());
            fmDTO.setComments(comments);

            fileMetadataDTOList.add(fmDTO);
        }

        return fileMetadataDTOList;
    }

    public void deleteFile(Long id){
        fileMetadataRepository.deleteById(id);
    }

    public FileMetadata save(FileMetadata fileMetadata) {
        return fileMetadataRepository.save(fileMetadata);
    }

    public FileMetadata findById(Long id) {
        Optional<FileMetadata> optionalFm = fileMetadataRepository.findById(id);

        if (optionalFm.isPresent()) {
            return optionalFm.get();
        }

        return null;
    }

}
