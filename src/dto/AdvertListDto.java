package dto;

import com.telran.data.entity.Advert;

import java.util.List;
import java.util.stream.Collectors;

public class AdvertListDto {
    List<AdvertDto> list;

    public AdvertListDto() {
    }

    public AdvertListDto(List<AdvertDto> list) {
        this.list = list;
    }

    public List<AdvertDto> getList() {
        return list;
    }

    public void setList(List<AdvertDto> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return list.stream()
                .map(AdvertDto::toString)
                .collect(Collectors.joining(";"));
    }

}
