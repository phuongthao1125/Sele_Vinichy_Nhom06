package testdata;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CartTestData {

    private final Path dbPath;

    public CartTestData() {
        this(resolveDbPath());
    }

    CartTestData(Path dbPath) {
        this.dbPath = dbPath;
    }

    public void seedCart(String email, String password, CartLine... lines) {
        if (!Files.exists(dbPath)) {
            throw new IllegalStateException("Cannot find vinichy database: " + dbPath);
        }

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath.toString().replace('\\', '/'))) {
            conn.setAutoCommit(false);
            setBusyTimeout(conn);
            try {
                long accountId = ensureAccount(conn, email, password);
                long cartId = ensureCart(conn, accountId);

                deleteCartItems(conn, cartId);

                BigDecimal cartTotal = BigDecimal.ZERO;
                for (CartLine line : lines) {
                    ProductVariant variant = findVariant(conn, line.sku());
                    if (line.stock() != null) {
                        updateStock(conn, variant.id(), line.stock());
                    }
                    BigDecimal itemTotal = variant.price().multiply(BigDecimal.valueOf(line.quantity()));
                    insertCartItem(conn, cartId, variant.id(), line.quantity(), itemTotal);
                    cartTotal = cartTotal.add(itemTotal);
                }

                updateCartTotal(conn, cartId, cartTotal);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot seed cart test data", e);
        }
    }

    public static CartLine line(String sku, int quantity, int stock) {
        return new CartLine(sku, quantity, stock);
    }

    public static CartLine line(String sku, int quantity) {
        return new CartLine(sku, quantity, null);
    }

    private static Path resolveDbPath() {
        String configuredPath = System.getProperty("vinichy.db.path");
        if (configuredPath != null && !configuredPath.isBlank()) {
            return Paths.get(configuredPath).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.dir"))
                .resolve("../vinichy-shop/database.db")
                .toAbsolutePath()
                .normalize();
    }

    private void setBusyTimeout(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA busy_timeout = 5000");
        }
    }

    private long ensureAccount(Connection conn, String email, String password) throws SQLException {
        Long accountId = queryLong(conn, "SELECT id FROM tai_khoan WHERE ten_dang_nhap = ?", email);
        if (accountId != null) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE tai_khoan SET mat_khau = ? WHERE id = ?")) {
                ps.setString(1, password);
                ps.setLong(2, accountId);
                ps.executeUpdate();
            }
            return accountId;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO tai_khoan (ten_dang_nhap, mat_khau) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        Long createdId = queryLong(conn, "SELECT id FROM tai_khoan WHERE ten_dang_nhap = ?", email);
        if (createdId == null) {
            throw new SQLException("Cannot create account for " + email);
        }
        return createdId;
    }

    private long ensureCart(Connection conn, long accountId) throws SQLException {
        Long cartId = queryLong(conn, "SELECT id FROM gio_hang WHERE tk_id = ?", accountId);
        if (cartId != null) {
            return cartId;
        }

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO gio_hang (tk_id, tong_tien_tam_tinh) VALUES (?, 0)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, accountId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }

        Long createdId = queryLong(conn, "SELECT id FROM gio_hang WHERE tk_id = ?", accountId);
        if (createdId == null) {
            throw new SQLException("Cannot create cart for account " + accountId);
        }
        return createdId;
    }

    private void deleteCartItems(Connection conn, long cartId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM gio_hang_ct WHERE gio_hang_id = ?")) {
            ps.setLong(1, cartId);
            ps.executeUpdate();
        }
    }

    private ProductVariant findVariant(Connection conn, String sku) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT ct.id, sp.gia_ban " +
                        "FROM san_pham_ct ct " +
                        "JOIN san_pham sp ON sp.id = ct.sp_id " +
                        "WHERE replace(ct.sku, ' ', '') = ?")) {
            ps.setString(1, normalizeSku(sku));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ProductVariant(rs.getLong("id"), rs.getBigDecimal("gia_ban"));
                }
            }
        }
        throw new SQLException("Cannot find product variant for SKU: " + sku);
    }

    private void updateStock(Connection conn, long sanPhamCtId, int stock) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE san_pham_ct SET so_luong_ton = ? WHERE id = ?")) {
            ps.setInt(1, stock);
            ps.setLong(2, sanPhamCtId);
            ps.executeUpdate();
        }
    }

    private void insertCartItem(Connection conn, long cartId, long sanPhamCtId, int quantity, BigDecimal itemTotal)
            throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO gio_hang_ct (gio_hang_id, sp_ct_id, so_luong_du_dinh, thanh_tien) VALUES (?, ?, ?, ?)")) {
            ps.setLong(1, cartId);
            ps.setLong(2, sanPhamCtId);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, itemTotal);
            ps.executeUpdate();
        }
    }

    private void updateCartTotal(Connection conn, long cartId, BigDecimal cartTotal) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE gio_hang SET tong_tien_tam_tinh = ? WHERE id = ?")) {
            ps.setBigDecimal(1, cartTotal);
            ps.setLong(2, cartId);
            ps.executeUpdate();
        }
    }

    private Long queryLong(Connection conn, String sql, Object value) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (value instanceof String stringValue) {
                ps.setString(1, stringValue);
            } else if (value instanceof Long longValue) {
                ps.setLong(1, longValue);
            } else {
                ps.setObject(1, value);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : null;
            }
        }
    }

    private String normalizeSku(String sku) {
        return sku.replace(" ", "");
    }

    public record CartLine(String sku, int quantity, Integer stock) {
        public CartLine {
            if (sku == null || sku.isBlank()) {
                throw new IllegalArgumentException("SKU must not be blank");
            }
            if (quantity < 1) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
        }
    }

    private record ProductVariant(long id, BigDecimal price) {
    }
}
