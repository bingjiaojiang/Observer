package com.future.observermonitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.future.observercommon.dto.DeviceDTO;
import com.future.observercommon.dto.ImgBasePath;
import com.future.observercommon.util.BeanUtil;
import com.future.observercommon.util.DateUtil;
import com.future.observercommon.util.FileUtil;
import com.future.observercommon.util.JacksonUtil;
import com.future.observermonitor.dto.StatisticDTO;
import com.future.observermonitor.mapper.IllegalInfoMapper;
import com.future.observermonitor.mapper.ImgMapper;
import com.future.observermonitor.po.Device;
import com.future.observermonitor.po.IllegalInfo;
import com.future.observermonitor.po.Img;
import com.future.observermonitor.service.*;
import com.future.observermonitor.vo.IllegalInfoVO;
import com.future.observermonitor.vo.ImgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MonitorServiceImpl implements MonitorService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private BaiDuAIService baiDuAIService;

    @Autowired
    private StatisticForUserService publicStatisticForUserService;

    @Autowired
    private StatisticForDeviceService publicStatisticForDeviceService;

    @Autowired
    @SuppressWarnings("all")
    private ImgMapper imgMapper;

    @Autowired
    @SuppressWarnings("all")
    private IllegalInfoMapper illegalInfoMapper;

    @Autowired
    private ImgBasePath imgBasePath;

    @Override
    public List<ImgVO> listOfImgVO(DeviceDTO deviceDTO) throws IOException {
        List<ImgVO> imgVOList = new LinkedList<>();

        List<Img> imgList = imgMapper.selectPageByDeviceSerial(new Page<>(1, 20), deviceDTO.getDeviceSerial());

        for (Img img : imgList) {
            // ???????????????????????????????????????????????????
            List<IllegalInfo> illegalInfoList = illegalInfoMapper.selectList(
                    new QueryWrapper<IllegalInfo>()
                            .eq("img_id", img.getId())
            );

            List<IllegalInfoVO> illegalInfoVOList = new ArrayList<>();
            BeanUtil.copyListProp(illegalInfoVOList, illegalInfoList, IllegalInfoVO.class);

            ImgVO imgVO = new ImgVO();
            BeanUtil.copyBeanProp(imgVO, img);
            imgVO.setBase64OfImg(Base64Utils.encodeToString(FileUtil.readFileAsBytes(img.getPath())));
            imgVO.setIllegalInfoList(illegalInfoVOList);

            imgVOList.add(imgVO);
        }

        return imgVOList;
    }

    @Override
    public ImgVO check(DeviceDTO deviceDTO) throws Exception {
        // ??????????????????????????????
        byte[] monitorImg = FileUtil.receiveFile(deviceDTO.getPicUrl());
        // ????????????
        String detectionResult = (String) baiDuAIService.check(deviceDTO).getResult();

        // ???????????????????????????????????????
        Device device = deviceService.getOne(new QueryWrapper<Device>().eq("device_serial", deviceDTO.getDeviceSerial()));

        // ???????????????????????????
        Set<String> illegalType = new HashSet<>();

        // ???????????????????????????????????????
        List<IllegalInfo> illegalInfoList = new LinkedList<>();

        // ?????????????????????????????????
        StatisticDTO statisticDTO = new StatisticDTO();

        /*
         * ????????????????????????????????????
         * ????????????????????????????????????????????????
         * ?????????????????????????????????????????????????????????????????????
         */
        JsonNode personInfo = JacksonUtil.jsonNodeOf(detectionResult, "person_info");
        for (int i = 0; i < personInfo.size(); i++) {
            JsonNode attributes = JacksonUtil.jsonNodeOf(personInfo.get(i), "attributes");

            IllegalInfo illegalInfo = new IllegalInfo();

            /*
             * ??????????????????
             */
            JsonNode location = JacksonUtil.jsonNodeOf(personInfo.get(i), "location");
            illegalInfo.setLocHeight(location.path("height").asInt());
            illegalInfo.setLocWidth(location.path("width").asInt());
            illegalInfo.setLocLeft(location.path("left").asInt());
            illegalInfo.setLocTop(location.path("top").asInt());
            /*
             * ??????
             */
            illegalInfo.setGender(JacksonUtil.jsonNodeOf(attributes, "gender", "name").asText());
            double genderScore = JacksonUtil.jsonNodeOf(attributes, "gender", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setAge(JacksonUtil.jsonNodeOf(attributes, "age", "name").asText());
            double ageScore = JacksonUtil.jsonNodeOf(attributes, "age", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setUpperWear(JacksonUtil.jsonNodeOf(attributes, "upper_wear", "name").asText());
            double upperWearScore = JacksonUtil.jsonNodeOf(attributes, "upper_wear", "score").asDouble();
            /*
             * ??????????????????
             */
            illegalInfo.setUpperColor(JacksonUtil.jsonNodeOf(attributes, "upper_color", "name").asText());
            double upperColorScore = JacksonUtil.jsonNodeOf(attributes, "upper_color", "score").asDouble();
            /*
             * ??????????????????
             */
            illegalInfo.setUpperWearTexture(JacksonUtil.jsonNodeOf(attributes, "upper_wear_texture", "name").asText());
            double upperWearTextureScore = JacksonUtil.jsonNodeOf(attributes, "upper_wear_texture", "score").asDouble();
            /*
             * ?????????????????????
             */
            illegalInfo.setUpperWearFg(JacksonUtil.jsonNodeOf(attributes, "upper_wear_fg", "name").asText());
            double upperWearFgScore = JacksonUtil.jsonNodeOf(attributes, "upper_wear_fg", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setLowerWear(JacksonUtil.jsonNodeOf(attributes, "lower_wear", "name").asText());
            double lowerWearScore = JacksonUtil.jsonNodeOf(attributes, "lower_wear", "score").asDouble();
            /*
             * ??????????????????
             */
            illegalInfo.setLowerColor(JacksonUtil.jsonNodeOf(attributes, "lower_color", "name").asText());
            double lowerColorScore = JacksonUtil.jsonNodeOf(attributes, "lower_color", "score").asDouble();
            /*
             * ???????????????
             */
            illegalInfo.setHeadWear(JacksonUtil.jsonNodeOf(attributes, "headwear", "name").asText());
            double headWearScore = JacksonUtil.jsonNodeOf(attributes, "headwear", "score").asDouble();
            /*
             * ???????????????
             */
            illegalInfo.setGlasses(JacksonUtil.jsonNodeOf(attributes, "glasses", "name").asText());
            double glassesScore = JacksonUtil.jsonNodeOf(attributes, "glasses", "score").asDouble();
            /*
             * ???????????????
             */
            illegalInfo.setBag(JacksonUtil.jsonNodeOf(attributes, "bag", "name").asText());
            double bagScore = JacksonUtil.jsonNodeOf(attributes, "bag", "score").asDouble();
            /*
             * ???????????????
             */
            illegalInfo.setFaceMask(JacksonUtil.jsonNodeOf(attributes, "face_mask", "name").asText());
            double faceMaskScore = JacksonUtil.jsonNodeOf(attributes, "face_mask", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setOrientation(JacksonUtil.jsonNodeOf(attributes, "orientation", "name").asText());
            double orientationScore = JacksonUtil.jsonNodeOf(attributes, "orientation", "score").asDouble();
            /*
             * ??????????????????
             */
            illegalInfo.setCellphone(JacksonUtil.jsonNodeOf(attributes, "cellphone", "name").asText());
            double cellphoneScore = JacksonUtil.jsonNodeOf(attributes, "cellphone", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setSmoke(JacksonUtil.jsonNodeOf(attributes, "smoke", "name").asText());
            double smokeScore = JacksonUtil.jsonNodeOf(attributes, "smoke", "score").asDouble();
            /*
             * ??????????????????
             */
            illegalInfo.setCarryingItem(JacksonUtil.jsonNodeOf(attributes, "carrying_item", "name").asText());
            double carryingItemScore = JacksonUtil.jsonNodeOf(attributes, "carrying_item", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setUmbrella(JacksonUtil.jsonNodeOf(attributes, "umbrella", "name").asText());
            double umbrellaScore = JacksonUtil.jsonNodeOf(attributes, "umbrella", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setVehicle(JacksonUtil.jsonNodeOf(attributes, "vehicle", "name").asText());
            double vehicleScore = JacksonUtil.jsonNodeOf(attributes, "vehicle", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setOcclusion(JacksonUtil.jsonNodeOf(attributes, "occlusion", "name").asText());
            double occlusionScore = JacksonUtil.jsonNodeOf(attributes, "occlusion", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setUpperCut(JacksonUtil.jsonNodeOf(attributes, "upper_cut", "name").asText());
            double upperCutScore = JacksonUtil.jsonNodeOf(attributes, "upper_cut", "score").asDouble();
            /*
             * ????????????
             */
            illegalInfo.setLowerCut(JacksonUtil.jsonNodeOf(attributes, "lower_cut", "name").asText());
            double lowerCutScore = JacksonUtil.jsonNodeOf(attributes, "lower_cut", "score").asDouble();
            /*
             * ?????????????????????????????????????????????????????????/???????????????????????????????????????????????????
             */
            illegalInfo.setIsHuman(JacksonUtil.jsonNodeOf(attributes, "is_human", "name").asText());
            double isHumanScore = JacksonUtil.jsonNodeOf(attributes, "is_human", "score").asDouble();

            /*
             * ????????????
             */
            // ????????????????????????
            Field[] fieldsOfIllegalInfo = illegalInfo.getClass().getDeclaredFields();
            // ????????????
            Field[] fieldsOfDevice = device.getClass().getDeclaredFields();
            // ??????????????????
            Field[] fieldsOfStatistic = statisticDTO.getClass().getDeclaredFields();

            boolean flag = false; // ????????????????????????????????????
            for (int j = 7; j < fieldsOfIllegalInfo.length - 1; j++) {
                Field fieldOfIllegalInfo = fieldsOfIllegalInfo[j];
                fieldOfIllegalInfo.setAccessible(true);
                String fieldValueOfIllegalInfo = (String) fieldOfIllegalInfo.get(illegalInfo);

                Field fieldOfDevice = fieldsOfDevice[j + 1];
                fieldOfDevice.setAccessible(true);
                String fieldValueOfDevice = (String) fieldOfDevice.get(device);

                if (fieldValueOfDevice != null && fieldValueOfDevice.contains(fieldValueOfIllegalInfo)) { // ??????????????????
                    // ??????????????????
                    illegalType.add(fieldValueOfIllegalInfo);

                    /*
                     * ????????????????????? + 1
                     */
                    Field fieldOfStatistic = fieldsOfStatistic[j + 1];
                    fieldOfStatistic.setAccessible(true);
                    Integer fieldValueOfStatistic = (Integer) fieldOfStatistic.get(statisticDTO);
                    fieldValueOfStatistic = fieldValueOfStatistic == null ? 1 : fieldValueOfStatistic + 1;
                    fieldOfStatistic.set(statisticDTO, fieldValueOfStatistic);

                    /*
                     * ???????????? + 1??????????????? + 1
                     */
                    Integer totalNum = statisticDTO.getTotalNum();
                    totalNum = totalNum == null ? 1 : totalNum + 1;
                    statisticDTO.setTotalNum(totalNum);
                    statisticDTO.setUntreatedNum(totalNum);

                    flag = true;
                }
            }

            if (flag) {
                illegalInfoList.add(illegalInfo);
            }
        }

        /*
         * ??????????????????
         */
        if (illegalInfoList.size() > 0) {
            // ????????????????????????
            File illegalImg = FileUtil.createFile(imgBasePath.getMonitorPath() + deviceDTO.getDeviceSerial() + "/" + DateUtil.getNow() + ".jpg");
            FileUtil.copy(monitorImg, illegalImg);

            String stringOfIllegalType = illegalType.toString();
            stringOfIllegalType = stringOfIllegalType.substring(1, stringOfIllegalType.length() - 1); // ??????"["???"]"

            Img img = new Img();
            img.setPath(illegalImg.getPath());
            img.setIllegalType(stringOfIllegalType);
            img.setDeviceSerial(deviceDTO.getDeviceSerial());
            imgMapper.insertByDeviceSerial(img);

            // ??????????????????
            for (IllegalInfo illegalInfo : illegalInfoList) {
                illegalInfo.setImgId(img.getId());
                illegalInfoMapper.insert(illegalInfo);
            }

            // ????????????
            statisticDTO.setUsername(deviceDTO.getUsername());
            statisticDTO.setDeviceSerial(deviceDTO.getDeviceSerial());
            statisticDTO.setDate(DateUtil.toDate(img.getCreateTime().toString(), "yyyy-MM-dd"));
            publicStatisticForDeviceService.add(statisticDTO);
            publicStatisticForUserService.add(statisticDTO);
        }

        /*
         * ??????????????????
         */
        ImgVO imgVO = new ImgVO();

        List<IllegalInfoVO> illegalInfoVOList = new ArrayList<>(illegalInfoList.size());
        BeanUtil.copyListProp(illegalInfoVOList, illegalInfoList, IllegalInfoVO.class);

        imgVO.setIllegalInfoList(illegalInfoVOList);

        return imgVO;
    }
}
