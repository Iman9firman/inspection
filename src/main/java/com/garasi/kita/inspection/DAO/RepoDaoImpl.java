package com.garasi.kita.inspection.DAO;

import com.garasi.kita.inspection.model.Inspection;
import com.garasi.kita.inspection.model.InspectionDetail;
import com.garasi.kita.inspection.model.PhotoItem;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RepoDaoImpl implements RepoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String loginQuery(String username, String password) {
        try {
            String query = "SELECT password FROM users WHERE username = '" + username + "' and role ='ROLE_APPS' and enabled = 1  LIMIT 1;";
            return jdbcTemplate.queryForObject(query, String.class);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public List<Inspection> listTask(String username) {
        List<Inspection> inspectionList = new ArrayList<>();
        String query = "SELECT * FROM `inspection` WHERE `inspektor` LIKE '%" + username + "%';";
        try {
            inspectionList = jdbcTemplate.query(query, new Object[]{}, BeanPropertyRowMapper.newInstance(Inspection.class));
            return inspectionList;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Error get task ", e);
        }
        return inspectionList;
    }

    @Override
    public Inspection getDataInspection(String kode) {
        try {
            String query = "SELECT * FROM `inspection` WHERE `kode_booking` = '" + kode + "' LIMIT 1;";
            return jdbcTemplate.queryForObject(query, BeanPropertyRowMapper.newInstance(Inspection.class));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public List<InspectionDetail> getDatainpectionDetailService(String kode) {
        List<InspectionDetail> inspectionDetailList = new ArrayList<>();
        String query = "SELECT * FROM `inspection_detail` WHERE `kode_booking` LIKE '%" + kode + "%';";
        try {
            inspectionDetailList = jdbcTemplate.query(query, new Object[]{}, BeanPropertyRowMapper.newInstance(InspectionDetail.class));
            return inspectionDetailList;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Error get task ", e);
        }
        return inspectionDetailList;
    }

    @Override
    public List<PhotoItem> getDataInspectionDetailPhoto(String kodeBooking, String idField) {
        List<PhotoItem> photo = new ArrayList<>();
        String query = "SELECT * FROM `photo_item` WHERE `kode_booking` = '" + kodeBooking + "' AND `id_field` = '" + idField + "'";
        try {
            photo = jdbcTemplate.query(query, new Object[]{}, BeanPropertyRowMapper.newInstance(PhotoItem.class));
            return photo;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error("Error get task ", e);
        }
        return photo;
    }

    @Override
    public void removePhoto(String name) {
        String query = "DELETE FROM `photo_item` WHERE `path` = '" + name + "'";
        jdbcTemplate.update(query);
    }

    @Override
    public int update(InspectionDetail data) {
        String query = "UPDATE inspection_detail set value='" + data.getValue() + "' WHERE kode_booking = '" + data.getKodeBooking() + "' and id_field = '" + data.getIdField() + "'";
        return jdbcTemplate.update(query);
    }

    @Override
    public int updateInspection(String kodeBooking) {
        String query = "UPDATE inspection set status= 2 WHERE kode_booking = '" + kodeBooking + "' ";
        return jdbcTemplate.update(query);
    }
}
