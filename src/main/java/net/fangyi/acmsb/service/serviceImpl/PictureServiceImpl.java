package net.fangyi.acmsb.service.serviceImpl;

import net.fangyi.acmsb.entity.Picture;
import net.fangyi.acmsb.repository.PictureRepository;
import net.fangyi.acmsb.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class PictureServiceImpl implements PictureService {

    @Autowired
    private PictureRepository pictureRepository;

    @Override
    public void updateImg(MultipartFile multipartFile) throws IOException {
        String name = multipartFile.getOriginalFilename();
        byte[] bytes = multipartFile.getBytes();
        Picture pic = new Picture();
        int pid = (int) (pictureRepository.count() + 1);
        pic.setPid(pid);
        pic.setName(name);
        pic.setPic_data(bytes);
        pictureRepository.save(pic);
    }

    @Override
    public byte[] getImg(int pid) {
        Picture pic = pictureRepository.findById(pid).orElse(null);
        if (pic != null) {
            return pic.getPic_data();
        }
        return null;
    }
}
