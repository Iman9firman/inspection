package com.garasi.kita.inspection.RTP.repositories;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.RTP.model.*;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class RtpRepositories {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public User loginQuery(String username) {
        try {
            String query = "SELECT password,role,name,detail FROM rtp_users WHERE username = '" + username + "' and enabled = 1  LIMIT 1;";
            return jdbcTemplate.queryForObject(query, BeanPropertyRowMapper.newInstance(User.class));
        } catch (Exception e) {
            return null;
        }

    }

    public int addProduct(Product product) throws SQLException {
        String query = "INSERT INTO rtp_barang (kodeBarang, namaBarang, stockAwal, hargaModal, hargaJual, create_by, create_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(query, product.getKodeBarang(), product.getNamaBarang(), product.getStockAwal(), product.getHargaModal(), product.getHargaJual(), product.getCreate_by(), product.getCreate_date());
    }

    public List<Product> listProduct() {
        String query = "SELECT * FROM rtp_barang where status = 1;";
        return jdbcTemplate.query(query, new RowMapper<Product>() {
            @Override
            public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("id");
                String kodeBarang = rs.getString("kodeBarang");
                String namaBarang = rs.getString("namaBarang");
                int stockAwal = rs.getInt("stockAwal");
                double hargaModal = rs.getDouble("hargaModal");
                double hargaJual = rs.getDouble("hargaJual");
                String kategori = rs.getString("kategori");
                String create_by = rs.getString("create_by");
                Timestamp create_date = rs.getTimestamp("create_date");

                return new Product(id, kodeBarang, namaBarang, stockAwal, hargaModal, hargaJual, kategori, create_by, create_date);
            }
        });
    }

    public List<StockInfo> getStockInfoByCurrentDate() {
        String query = "SELECT * FROM rtp_stockinfo WHERE tanggal = CURDATE() LIMIT 1";
        return jdbcTemplate.query(query, new RowMapper<StockInfo>() {
            @Override
            public StockInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
                int id = rs.getInt("id");
                Timestamp tanggal = rs.getTimestamp("tanggal");
                String kategori = rs.getString("kategori");
                int jumlahMasuk = rs.getInt("jumlahMasuk");
                int jumlahKeluar = rs.getInt("jumlahKeluar");
                double omset = rs.getDouble("omset");
                double untung = rs.getDouble("untung");
                return new StockInfo(id, tanggal, kategori, jumlahMasuk, jumlahKeluar, omset, untung);
            }
        });
    }

    public List<Stock> getStockByBranch(int branch) {
        String query = "SELECT s.stock_id, p.id , p.namaBarang, s.good,s.bad, g.branch_name\n" +
                "FROM rtp_stock s\n" +
                "INNER JOIN rtp_barang p ON s.product_id = p.id\n" +
                "INNER JOIN rtp_kacab g ON s.branch_id= g.branch_id where  s.branch_id = ? ;";

        return jdbcTemplate.query(query, new RowMapper<Stock>() {
            @Override
            public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
                Stock stock = new Stock();
                stock.setStock_id(rs.getInt("stock_id"));
                stock.setProduct(new Product(rs.getInt("id"), rs.getString("namaBarang")));
                stock.setGood(rs.getInt("good"));
                stock.setBad(rs.getInt("bad"));
                stock.setKacab(new Kacab(rs.getString("g.branch_name")));
                return stock;
            }
        }, new Object[]{branch});
    }

    public List<Stock> getStockAllBranch() {
        String query = "SELECT s.stock_id,p.id, p.namaBarang, s.good,s.bad, g.branch_name as namaCabang\n" +
                "FROM rtp_stock s\n" +
                "INNER JOIN rtp_barang p ON s.product_id = p.id\n" +
                "INNER JOIN rtp_kacab g ON s.branch_id= g.branch_id where  s.branch_id != 1 ;";

        return jdbcTemplate.query(query, new RowMapper<Stock>() {
            @Override
            public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
                Stock stock = new Stock();
                stock.setStock_id(rs.getInt("stock_id"));
                stock.setProduct(new Product(rs.getInt("id"), rs.getString("namaBarang")));
                stock.setGood(rs.getInt("good"));
                stock.setBad(rs.getInt("bad"));
                stock.setKacab(new Kacab(rs.getString("namaCabang")));
                return stock;
            }
        });
    }


    public List<HistoryStock> getStockHistoryByBranch(int from) {
        String query = "SELECT * FROM  rtp_history_stock\n" +
                "WHERE tanggal >= DATE_SUB(NOW(), INTERVAL 30 DAY) AND tanggal <= NOW()  AND branch_id = ? order by tanggal desc ;";
        return jdbcTemplate.query(query, new RowMapper<HistoryStock>() {
            @Override
            public HistoryStock mapRow(ResultSet rs, int rowNum) throws SQLException {
                HistoryStock historyStock = new HistoryStock();
                historyStock.setTanggal(rs.getTimestamp("tanggal"));
                historyStock.setJenisTransaksi(rs.getString("jenis_transaksi"));
                historyStock.setUsername(rs.getString("username"));

                // Mendapatkan data dari kolom 'detail' dalam format JSON
                String detailJson = rs.getString("detail");

                // Mengonversi data JSON ke List<DetailHistoryStock>
                List<DetailHistoryStock> detailList = new ArrayList<>();
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    TypeReference<List<DetailHistoryStock>> typeReference = new TypeReference<>() {
                    };
                    detailList = objectMapper.readValue(detailJson, typeReference);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                historyStock.setDetailList(detailList);
                return historyStock;

            }
        }, new Object[]{from});
    }

    public int insertStock(int product_id, int good, int bad, int branch_id) throws SQLException {
        String query = "INSERT INTO rtp_stock (product_id, good, bad, branch_id) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "good = good + VALUES(good), " + // Menambahkan nilai good dengan nilai parameter
                "bad = bad + VALUES(bad)"; // Menambahkan nilai bad dengan nilai parameter
        return jdbcTemplate.update(query, product_id, good, bad, branch_id);
    }

    public int insertHistory(String username, String jenisTrx, JSONArray detail, int branch_id) {
        String query = "INSERT INTO `rtp_history_stock` (`tanggal`, `username`, `jenis_transaksi`, `detail`, `branch_id`) VALUES " +
                "(now(),?,?,?,?);";
        return jdbcTemplate.update(query, new Object[]{username, jenisTrx, detail.toString(), branch_id});
    }

    public List<User> listUserStaff() {
        String query = "SELECT *\n" +
                "FROM `rtp_users`\n" +
                "WHERE  `role` = 'ROLE_PENGURUS' ;";
        return jdbcTemplate.query(query, BeanPropertyRowMapper.newInstance(User.class));
    }

    @Transactional
    public void tambahTransaksi(TransaksiRequest transaksiRequest) {
        Transaksi transaksi = transaksiRequest.getTransaksi();
        List<TransaksiDetail> transaksiDetails = transaksiRequest.getTransaksiDetails();

        transaksi.setTransaksiId(transaksi.generateTransaksiId(transaksi.getBranchId(), new Date()));
        String sqlTransaksi = "INSERT INTO rtp_transaksi (transaksi_id,branch_id, created_by, tanggal_transaksi, keterangan, total_transaksi) VALUES (?,?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlTransaksi, new Object[]{transaksi.getTransaksiId(), transaksi.getBranchId(), transaksi.getCreatedBy(), transaksi.getTanggalTransaksi(), transaksi.getKeterangan(), transaksi.getTotalTransaksi()});
        String sqlTransaksiDetail = "INSERT INTO rtp_transaksi_detail (transaksi_id, pengambilan_barang, barang_terjual, sisa_barang, barang_reject, total_harga, product_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        for (TransaksiDetail transaksiDetail : transaksiDetails) {
            jdbcTemplate.update(sqlTransaksiDetail, new Object[]{transaksi.getTransaksiId(), transaksiDetail.getPengambilanBarang(), transaksiDetail.getBarangTerjual(), transaksiDetail.getSisaBarang(), transaksiDetail.getBarangReject(), transaksiDetail.getTotalHarga(), transaksiDetail.getProductId()});
        }
    }


    public List<Transaksi> getAllTransaksi() {
        String sql = "SELECT * FROM rtp_transaksi";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Transaksi.class));
    }

    public List<TransaksiDetail> getTransaksiDetailsByTransaksiId(String transaksiId) {
        String sql = "SELECT * FROM rtp_transaksi_detail WHERE transaksi_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TransaksiDetail.class), transaksiId);
    }

    public List<TransaksiRequest> mapToTransaksiRequestList(List<Transaksi> transaksiList) {
        List<TransaksiRequest> transaksiRequestList = new ArrayList<>();
        for (Transaksi transaksi : transaksiList) {
            List<TransaksiDetail> transaksiDetails = getTransaksiDetailsByTransaksiId(transaksi.getTransaksiId());
            TransaksiRequest transaksiRequest = new TransaksiRequest(transaksi, transaksiDetails);
            transaksiRequestList.add(transaksiRequest);
        }
        return transaksiRequestList;
    }

    public void updateTransaksiByid(TransaksiDetail transaksiDetail) {
        String query = "UPDATE rtp_transaksi_detail " +
                "SET barang_terjual = ?, " +
                "sisa_barang = ?, " +
                "barang_reject = ?, " +
                "total_harga = ? " +
                "WHERE transaksi_id = ? AND product_id = ?";

        jdbcTemplate.update(query, new Object[]{
                transaksiDetail.getPengambilanBarang(),
                transaksiDetail.getSisaBarang(),
                transaksiDetail.getBarangReject(),
                transaksiDetail.getTotalHarga(),
                transaksiDetail.getTransaksiId(),
                transaksiDetail.getProductId()});

    }

    public void updateStock(int branchId, TransaksiDetail transaksiDetail) {
        String query = "UPDATE `rtp_stock` AS s " +
                "JOIN rtp_barang AS b ON s.product_id = b.id \n" +
                "SET  `good` = `good` + ? ,  `bad` =`bad` + ? " +
                "WHERE s.branch_id = ? AND b.kodeBarang = ? ;";
        jdbcTemplate.update(query, transaksiDetail.getSisaBarang(), transaksiDetail.getBarangReject(), branchId, transaksiDetail.getProductId());
    }

    public void ambilStockPenjualan(int branchId, TransaksiDetail transaksiDetail) {
        String query = "UPDATE rtp_stock AS s\n" +
                "JOIN rtp_barang AS b ON s.product_id = b.id SET\n" +
                " `good` = `good` - ? " +
                " WHERE s.branch_id = ? AND b.kodeBarang = ?";
        jdbcTemplate.update(query, transaksiDetail.getPengambilanBarang(), branchId, transaksiDetail.getProductId());
    }

    @Transactional
    public void updateTransaksi(int status, TransaksiRequest transaksiRequest) {
        String sqlTransaksi = "UPDATE `rtp_transaksi` SET `status` = ? WHERE `transaksi_id` = ?";
        jdbcTemplate.update(sqlTransaksi, new Object[]{status, transaksiRequest.getTransaksi().getTransaksiId()});

    }
}
