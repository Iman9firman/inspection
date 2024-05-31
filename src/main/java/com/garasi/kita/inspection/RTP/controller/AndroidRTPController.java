package com.garasi.kita.inspection.RTP.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garasi.kita.inspection.RTP.config.InvoiceIdGenerator;
import com.garasi.kita.inspection.RTP.model.*;
import com.garasi.kita.inspection.RTP.repositories.RtpRepositories;
import com.garasi.kita.inspection.model.Inspection;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
                rtpRepositories.insertStock(addProduct, stockAwal, 0, 1);
                HashMap<String, Object> stock = new HashMap<>();
                stock.put("good", stockAwal);
                stock.put("bad", 0);
                stock.put("product_id", addProduct);
                stock.put("product_name", namaBarang);

                rtpRepositories.insertHistory("Admin", "Masuk", new JSONArray().put(stock), 1);
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


    @PostMapping("/getDataGudang")
    public ResponseEntity<Object> getDataGudang(HttpServletRequest request, @RequestParam int from) {

        HashMap<String, Object> result = new HashMap<>();
        List<Stock> stockInfoList = rtpRepositories.getStockByBranch(from);
        if (stockInfoList != null && !stockInfoList.isEmpty()) {
            result.put("data", stockInfoList);
        }
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
                System.out.println("ada error>>" + exception.getMessage());
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
            System.out.println("ada error2>>" + exception.getMessage());
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
        //ceck stock dlu
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, Integer> stockGudang = new HashMap<>();

        List<Stock> stockInfoList;

        stockInfoList = rtpRepositories.getStockByBranch(transaksiRequest.getTransaksi().getBranchId());
        if (stockInfoList.size() == 0 || stockInfoList == null) {
            HashMap<String, Object> stock = new HashMap<>();
            result.put("status", "erorr");
            result.put("data", "Tidak Ada Stock");
            return new ResponseEntity<>(stock, HttpStatus.OK);
        } else {
            String errornya = "";
            for (Stock stock : stockInfoList) {
                stockGudang.put(stock.getProduct().getNamaBarang(), stock.getGood());
            }

            for (TransaksiDetail detailHistoryStock : transaksiRequest.getTransaksiDetails()) {
                try {
                    Integer sisaStock = stockGudang.get(detailHistoryStock.getNamaBarang());
                    if (detailHistoryStock.getPengambilanBarang() > sisaStock) {
                        errornya += detailHistoryStock.getNamaBarang() + " tersisa " + sisaStock + ", ";
                    }

                } catch (Exception exception) {
                    result.put("status", "erorr");
                    result.put("data", exception.getMessage());
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            }

            if (!errornya.equalsIgnoreCase("")) {
                result.put("status", "eror");
                result.put("data", errornya + "di Gudang.");
                return new ResponseEntity<>(result, HttpStatus.OK);
            }
        }

        rtpRepositories.tambahTransaksi(transaksiRequest);
        List<TransaksiDetail> transaksiDetail = transaksiRequest.getTransaksiDetails();
        JSONArray detailArray = new JSONArray();
        for (TransaksiDetail detail : transaksiDetail) {
            rtpRepositories.ambilStockPenjualan(transaksiRequest.getTransaksi().getBranchId(), detail);
            HashMap<String, Object> stock = new HashMap<>();
            stock.put("good", -detail.getPengambilanBarang());
            stock.put("product_id", detail.getProductId());
            stock.put("product_name", detail.getNamaBarang());
            detailArray.put(stock);
        }
        rtpRepositories.insertHistory(transaksiRequest.getTransaksi().getTransaksiId(), "Keluar", detailArray, transaksiRequest.getTransaksi().getBranchId());

        result.put("data", "success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/updateTransaksi/{branchid}")
    public ResponseEntity<Object> updateTransaksi(@PathVariable int branchid, @RequestBody TransaksiRequest transaksiRequest) {

        int total = 0;
        List<TransaksiDetail> transaksiDetail = transaksiRequest.getTransaksiDetails();
        for (TransaksiDetail detail : transaksiDetail) {
            rtpRepositories.updateTransaksiByid(detail);
            rtpRepositories.updateStock(branchid, detail);
            total += detail.getBarangTerjual();
        }

        rtpRepositories.updateTransaksi(1, total, transaksiRequest);

        HashMap<String, Object> result = new HashMap<>();
        result.put("data", "success");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getHistoryTransaksi/{branchid}")
    public ResponseEntity<Object> getHistoryTransaksi(@PathVariable int branchid) {

        List<Transaksi> transaksiList = rtpRepositories.getAllTransaksiPenjualan(branchid);
        if (transaksiList.size() == 0) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("data", new HashMap<>());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        List<TransaksiRequest> transaksiRequestList = rtpRepositories.mapToTransaksiRequestList(transaksiList);

        HashMap<String, Object> result = new HashMap<>();
        result.put("data", transaksiRequestList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/getHistoryPenjualan/{branchid}")
    public ResponseEntity<Object> getHistoryPenjualan(@PathVariable int branchid) {

        List<Invoice> invoiceList = rtpRepositories.getHistoryPenjualan(branchid);
        if (invoiceList.size() == 0) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("data", new JSONArray());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("data", invoiceList);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/getInvoceDetail")
    public ResponseEntity<Object> getInvoceDetail(@RequestParam String id) {
        Invoice invoice = rtpRepositories.invoiceById(id);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PostMapping("/catatanPenjualan")
    public ResponseEntity<Object> catatanPenjualan() {
        int totalBalance = 0;
        List<Penjualan.History> historyList = new ArrayList<>();
        List<Penjualan.Setoran> setoranList = new ArrayList<>();
        for (PenjualanDetail obj : rtpRepositories.getInvoceDay()) {
            if (obj.getStatus().equalsIgnoreCase("2")) {
                totalBalance += obj.getGrandTotal();
                historyList.add(new Penjualan.History(obj.getStaf(), obj.getInvoiceId(), new Date(), obj.getGrandTotal()));
            } else if (obj.getStatus().equalsIgnoreCase("1")) {
                setoranList.add(new Penjualan.Setoran(obj.getStaf() + "(" + obj.getBranchName() + ")", obj.getGrandTotal()));
            }
        }
        Penjualan penjualan = new Penjualan(totalBalance, setoranList, historyList);

        return new ResponseEntity<>(penjualan, HttpStatus.OK);
    }


    @PostMapping("/generateInvoce/{date}/{branchid}")
    public ResponseEntity<Object> generateInvoce(@PathVariable String date, @PathVariable int branchid, @RequestParam String name) {
        List<InvoiceDetail> invoiceDetailList = rtpRepositories.getInvoiceDetail(date, branchid);
        List<String> errorList = new ArrayList<>();
        if (invoiceDetailList.size() == 0) {
            errorList.add("belum ada transaki / data");
        }
        for (InvoiceDetail invoiceDetail : invoiceDetailList) {
            if (invoiceDetail.getStatus() == 0) {
                if (!errorList.contains(invoiceDetail.getCreatedBy())) {
                    errorList.add(invoiceDetail.getCreatedBy());
                }
            }
        }

        if (errorList.size() > 0) {
            String error = errorList.stream().collect(Collectors.joining(","));
            return new ResponseEntity<>(error + " belum lengkap.", HttpStatus.OK);
        } else {
            Invoice invoice = calculateInvoices(branchid, invoiceDetailList);
            invoice.setBranchId(branchid);
            invoice.setStaf(name);
            invoice.setStatus("1");
            invoice.setCreateDate(new Date());
            rtpRepositories.insertInvoice(invoice);
            String trxId = "";
            for (InvoiceDetail invoiceDetail : invoiceDetailList) {
                if (!trxId.equalsIgnoreCase(invoiceDetail.getTransaksiId())) {
                    rtpRepositories.updateTransaksiByStatus(2, invoiceDetail.getTransaksiId(), invoice.getInvoiceId());
                    trxId = invoiceDetail.getTransaksiId();
                }
            }
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        }


    }

    public Invoice calculateInvoices(int branchId, List<InvoiceDetail> invoiceDetailList) {
        ObjectMapper objectMapper = new ObjectMapper();
        String transaksiId = InvoiceIdGenerator.generateInvoiceId(String.format("%03d", branchId));
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(transaksiId);
        BigDecimal grandTotal = BigDecimal.ZERO;
        BigDecimal modalTotal = BigDecimal.ZERO;
        Map<String, Object> detailMap = new HashMap<>();

        try {
            for (InvoiceDetail detail : invoiceDetailList) {

                grandTotal = grandTotal.add(detail.getHargaJual().multiply(BigDecimal.valueOf(detail.getBarangTerjual())));
                modalTotal = modalTotal.add(detail.getHargaModal().multiply(BigDecimal.valueOf(detail.getBarangTerjual())));

                HashMap<String, String> detailModel = (HashMap<String, String>) detailMap.getOrDefault(detail.getNamaBarang(), new HashMap<>());
                detailModel.put("j", String.valueOf(detail.getHargaJual()));
                detailModel.put("m", String.valueOf(detail.getHargaModal()));
                Integer terjual = Integer.valueOf(detailModel.getOrDefault("p", "0")) + detail.getBarangTerjual();
                detailModel.put("p", String.valueOf(terjual));
                detailModel.put("t", String.valueOf(detail.getHargaJual().multiply(BigDecimal.valueOf(terjual))));

                detailMap.put(detail.getNamaBarang(), detailModel);
            }
            invoice.setInvoiceList(objectMapper.writeValueAsString(detailMap));
            invoice.setModal(modalTotal);
            invoice.setGrandTotal(grandTotal);
            invoice.setUntung(grandTotal.subtract(modalTotal));

        } catch (
                Exception e) {
            e.printStackTrace();
        }

        return invoice;

    }


}

