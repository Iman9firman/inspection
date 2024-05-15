package com.garasi.kita.inspection.RTP.controller;

import com.garasi.kita.inspection.RTP.model.*;
import com.garasi.kita.inspection.RTP.repositories.RtpRepositories;
import com.garasi.kita.inspection.model.Inspection;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/rtp")
public class AndroidRTPController {

    @Autowired
    RtpRepositories rtpRepositories;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password, HttpServletRequest request) {
        HashMap<String, String> result = new HashMap<>();
        User user = rtpRepositories.loginQuery(username);
        if (user == null) {
            result = new HashMap<>();
            result.put("data", "User tidak terdaftar");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            if (user.getPassword().equalsIgnoreCase(password)) {
                result = new HashMap<>();
                result.put("data", "success");
                result.put("role", user.getRole());
                result.put("name", user.getName());
                result.put("branch", user.getDetail());
                return new ResponseEntity<>(result, HttpStatus.OK);
            } else {
                result = new HashMap<>();
                result.put("data", "Password salah");
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Object> addProduct(@RequestParam String user, @RequestParam String kodeBarang, @RequestParam String namaBarang, @RequestParam String kategori, @RequestParam int stockAwal, @RequestParam double hargaModal, @RequestParam double hargaJual,
                                             HttpServletRequest request) {
        HashMap<String, Object> result = new HashMap<>();
        Product product = new Product(kodeBarang, namaBarang, stockAwal, hargaModal, hargaJual, kategori, user, new Timestamp(System.currentTimeMillis()));
        int addProduct = 0;
        try {
            addProduct = rtpRepositories.addProduct(product);
            if (addProduct > 0) {
                result.put("status", "success");
                result.put("data", "Number of rows inserted: " + addProduct);
            } else {
                result.put("status", "failed");
                result.put("data", "Failed to insert product.");
            }
        } catch (Exception e) {
            result.put("status", "failed");
            result.put("data", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/getProduct")
    public ResponseEntity<Object> getProduct(HttpServletRequest request, @RequestParam String user) {
        HashMap<String, Object> result = new HashMap<>();
        List<Product> listTask = rtpRepositories.listProduct();
        result.put("data", listTask);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/stockInfo")
    public ResponseEntity<Object> getStockInfoByCurrentDate(HttpServletRequest request, @RequestParam String user) {

        HashMap<String, Object> result = new HashMap<>();
        List<StockInfo> stockInfoList = rtpRepositories.getStockInfoByCurrentDate();

        if (stockInfoList != null && !stockInfoList.isEmpty()) {
            for (StockInfo stockInfo : stockInfoList) {
                result.put("data", stockInfo);
            }
        } else {
            result.put("data", "No stock info found for current date.");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getStock")
    public ResponseEntity<Object> getStock(HttpServletRequest request, @RequestParam int from) {

        HashMap<String, Object> result = new HashMap<>();
        List<Stock> stockInfoList = rtpRepositories.getStockByBranch(from);

        if (stockInfoList != null && !stockInfoList.isEmpty()) {
            result.put("data", stockInfoList);
        } else {
            result.put("data", "No stock info found for current date.");
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/getStockAllBranch")
    public ResponseEntity<Object> getStockAllBranch(HttpServletRequest request) {
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, List<Stock>> kacabStock = new HashMap<>();
        List<Stock> stockInfoList = rtpRepositories.getStockAllBranch();
        String kacabIdLast = "";
        List<Stock> stockListTemp = new ArrayList<>();
        if (stockInfoList != null && !stockInfoList.isEmpty()) {
            for (Stock ss : stockInfoList) {
                if (!ss.getKacab().getBranch_name().equalsIgnoreCase(kacabIdLast)) {
                    kacabIdLast = ss.getKacab().getBranch_name();
                    stockListTemp = new ArrayList<>();
                }
                stockListTemp.add(ss);
                kacabStock.put(ss.getKacab().getBranch_name(), stockListTemp);
            }
        } else {
            result.put("data", "No stock info found for current date.");
        }
        result.put("data", kacabStock);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/getHistoryStock")
    public ResponseEntity<Object> getHistoryStock(HttpServletRequest request, @RequestParam int from) {

        HashMap<String, Object> result = new HashMap<>();
        List<HistoryStock> historyStocks = rtpRepositories.getStockHistoryByBranch(from);
        List<Stock> stockInfoList = rtpRepositories.getStockByBranch(from);
        if (stockInfoList != null && !stockInfoList.isEmpty()) {
            result.put("data", stockInfoList);
        }


        if (historyStocks != null && !historyStocks.isEmpty()) {
            result.put("history", historyStocks);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/AddStock")
    public ResponseEntity<Object> addStock(HttpServletRequest request, @RequestBody HistoryStock historyStock) {
        HashMap<String, Object> result = new HashMap<>();
        HashMap<Integer, Integer> stockGudang = new HashMap<>();
        JSONArray detail = new JSONArray();
        List<Stock> stockInfoList;
        if (historyStock.getBranch_id() > 1) {
            stockInfoList = rtpRepositories.getStockByBranch(1);
            if (stockInfoList.size() == 0 || stockInfoList == null) {
                HashMap<String, Object> stock = new HashMap<>();
                stock.put("error", "Tidak Ada Stock di Gudang");
                return new ResponseEntity<>(stock, HttpStatus.BAD_REQUEST);
            } else {
                String errornya = "";
                for (Stock stock : stockInfoList) {
                    stockGudang.put(stock.getProduct().getId(), stock.getGood());
                }

                for (DetailHistoryStock detailHistoryStock : historyStock.getDetailList()) {
                    try {
                        if (historyStock.getBranch_id() > 1) {
                            Integer sisaStock = stockGudang.get(detailHistoryStock.getProduct_id());
                            if (detailHistoryStock.getGood() > sisaStock) {
                                errornya += detailHistoryStock.getProduct_name() + " tersisa " + sisaStock + ", ";
                            }
                        }
                    } catch (Exception exception) {
                        result.put("status", "erorr");
                        result.put("error", exception.getMessage());
                        return new ResponseEntity<>(result, HttpStatus.OK);
                    }
                }

                if (!errornya.equalsIgnoreCase("")) {
                    result.put("status", "eror");
                    result.put("error", errornya + "di Gudang.");
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            }

        }

        for (DetailHistoryStock detailHistoryStock : historyStock.getDetailList()) {
            try {
                rtpRepositories.insertStock(detailHistoryStock.getProduct_id(), detailHistoryStock.getGood(), detailHistoryStock.getBad(), historyStock.getBranch_id());
                if (historyStock.getBranch_id() > 1) {
                    //update stock gudang
                    rtpRepositories.insertStock(detailHistoryStock.getProduct_id(), -detailHistoryStock.getGood(), -detailHistoryStock.getBad(), 1);
                }
                HashMap<String, Object> stock = new HashMap<>();
                stock.put("good", detailHistoryStock.getGood());
                stock.put("bad", detailHistoryStock.getBad());
                stock.put("product_id", detailHistoryStock.getProduct_id());
                stock.put("product_name", detailHistoryStock.getProduct_name());
                detail.put(stock);
            } catch (Exception exception) {
                result.put("status", "erorr");
                result.put("error", exception.getMessage());
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        try {
            //tambah history gudang
            if (historyStock.getBranch_id() > 1) {
                rtpRepositories.insertHistory(historyStock.getUsername(), "Keluar", detail, 1);
            }
            rtpRepositories.insertHistory(historyStock.getUsername(), historyStock.getJenisTransaksi(), detail, historyStock.getBranch_id());
        } catch (Exception exception) {
            result.put("status", "erorr");
            result.put("error", exception.getMessage());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }


        result.put("data", "success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getStaff")
    public ResponseEntity<Object> getStaff(HttpServletRequest request) {
        HashMap<String, Object> result = new HashMap<>();
        List<User> listTask = rtpRepositories.listUserStaff();
        result.put("data", listTask);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/postTransaksi")
    public ResponseEntity<Object> postTransaksi(@RequestBody TransaksiRequest transaksiRequest) {
        rtpRepositories.tambahTransaksi(transaksiRequest);
        List<TransaksiDetail> transaksiDetail = transaksiRequest.getTransaksiDetails();
        for (TransaksiDetail detail : transaksiDetail) {
            rtpRepositories.ambilStockPenjualan(transaksiRequest.getTransaksi().getBranchId(), detail);
        }
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", "success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/updateTransaksi/{branchid}")
    public ResponseEntity<Object> updateTransaksi(@PathVariable int branchid, @RequestBody TransaksiRequest transaksiRequest) {

        rtpRepositories.updateTransaksi(1, transaksiRequest);
        List<TransaksiDetail> transaksiDetail = transaksiRequest.getTransaksiDetails();
        for (TransaksiDetail detail : transaksiDetail) {
            rtpRepositories.updateTransaksiByid(detail);
            rtpRepositories.updateStock(branchid, detail);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("data", "success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getHistoryTransaksi")
    public ResponseEntity<Object> getHistoryTransaksi() {

        List<Transaksi> transaksiList = rtpRepositories.getAllTransaksi();
        List<TransaksiRequest> transaksiRequestList = rtpRepositories.mapToTransaksiRequestList(transaksiList);

        HashMap<String, Object> result = new HashMap<>();
        result.put("data", transaksiRequestList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
