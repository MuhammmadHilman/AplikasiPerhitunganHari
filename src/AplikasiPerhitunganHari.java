import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.temporal.ChronoUnit;


public class AplikasiPerhitunganHari extends javax.swing.JFrame {

    private final Locale indonesianLocale = new Locale("id", "ID");
    
    private boolean isUpdating = false;
    
    public AplikasiPerhitunganHari() {
        initComponents();
        
        // Set tahunSpinner untuk menggunakan SpinnerNumberModel, memastikan nilai berupa Integer
        tahunSpinner.setModel(new SpinnerNumberModel(2023, 1900, 2100, 1));
        
        // Gunakan variabel kontrol agar perubahan awal tidak memicu listener
        isUpdating = true;
        
        // Set tanggalAwalCalendar ke tanggal saat ini
        tanggalAwalCalendar.setDate(new Date());
        
        // Ambil tanggal saat ini
        LocalDate currentDate = LocalDate.now();

        // Sinkronkan tahunSpinner dan bulanComboBox dengan tanggal saat ini
        tahunSpinner.setValue(currentDate.getYear());
        bulanComboBox.setSelectedIndex(currentDate.getMonthValue() - 1);

        // Nonaktifkan variabel kontrol setelah inisialisasi
        isUpdating = false;
        
        // Event listener untuk bulanComboBox
        bulanComboBox.addActionListener(evt -> {
            if (!isUpdating) {
                updateCalendarFromSpinnerComboBox();
                hitungJumlahHariDanKabisat();
            }
        });
        
        // Event listener untuk tahunSpinner
        tahunSpinner.addChangeListener(evt -> {
            if (!isUpdating) {
                updateCalendarFromSpinnerComboBox();
                hitungJumlahHariDanKabisat();
            }
        });

        // Event listener untuk perubahan tanggal di tanggalAwalCalendar
        tanggalAwalCalendar.addPropertyChangeListener("calendar", evt -> {
            if (!isUpdating) {
            updateSpinnerComboBoxFromCalendar();
            hitungSelisihHari(); // Menghitung selisih hari setiap kali tanggal awal berubah
            }
        });

        // Event listener untuk perubahan tanggal di tanggalAkhirCalendar
        tanggalAkhirCalendar.addPropertyChangeListener("calendar", evt -> {
            hitungSelisihHari(); // Menghitung selisih hari setiap kali tanggal akhir berubah
        });
    }

        public int getJumlahHari(int tahun, int bulan) {
        YearMonth yearMonth = YearMonth.of(tahun, bulan); // bulan dalam bentuk 1-12
        return yearMonth.lengthOfMonth(); // Mengembalikan jumlah hari dalam bulan
    }
    
        public void hitungJumlahHari() {
        int bulan = bulanComboBox.getSelectedIndex() + 1;
        int tahun = (int) tahunSpinner.getValue();

        YearMonth yearMonth = YearMonth.of(tahun, bulan);
        int jumlahHari = yearMonth.lengthOfMonth();
        hasilLabel.setText("Jumlah Hari: " + jumlahHari);

        // Mendapatkan hari pertama dan terakhir dalam bulan tersebut
        LocalDate tanggalPertama = yearMonth.atDay(1);
        LocalDate tanggalTerakhir = yearMonth.atEndOfMonth();

        // Mengubah hari pertama dan terakhir ke bahasa Indonesia
        String hariPertama = tanggalPertama.getDayOfWeek().getDisplayName(TextStyle.FULL, indonesianLocale).toUpperCase();
        String hariTerakhir = tanggalTerakhir.getDayOfWeek().getDisplayName(TextStyle.FULL, indonesianLocale).toUpperCase();

        hariPertamaLabel.setText("Hari Pertama: " + hariPertama);
        hariTerakhirLabel.setText("Hari Terakhir: " + hariTerakhir);
    }
        
        public boolean isTahunKabisat(int tahun) {
        return Year.of(tahun).isLeap();
    }
        public void hitungJumlahHariDanKabisat() {
        int tahun = (int) tahunSpinner.getValue(); // Ambil tahun dari JSpinner
        int bulanIndex = bulanComboBox.getSelectedIndex(); // Ambil bulan dari JComboBox (0-11)
        String bulanNama = bulanComboBox.getItemAt(bulanIndex); // Ambil nama bulan dari JComboBox

        // Mengecek apakah tahun adalah tahun kabisat
        boolean kabisat = isTahunKabisat(tahun);
        String statusKabisat = kabisat ? "Tahun Kabisat" : "Bukan Tahun Kabisat";

        // Update label dengan informasi tahun dan bulan
        kabisatLabel.setText(statusKabisat + " - Tahun: " + tahun + ", Bulan: " + bulanNama);
    }
        
        private void updateCalendarFromSpinnerComboBox() {
        if (isUpdating) return; // Jika sedang memperbarui, keluar dari metode ini
        isUpdating = true;

        int bulan = bulanComboBox.getSelectedIndex(); // Bulan di JComboBox dimulai dari 0 (Januari)
        int tahun = (int) tahunSpinner.getValue(); // Ambil tahun sebagai Integer

        // Atur tanggal di JCalendar sesuai dengan bulan dan tahun yang dipilih
        LocalDate tanggalBaru = LocalDate.of(tahun, bulan + 1, 1); // bulan + 1 karena LocalDate mulai dari 1 (Januari)
        Date tanggal = Date.from(tanggalBaru.atStartOfDay(ZoneId.systemDefault()).toInstant());
        tanggalAwalCalendar.setDate(tanggal);

        isUpdating = false;
    }
        
        private void updateSpinnerComboBoxFromCalendar() {
        if (isUpdating) return; // Jika sedang memperbarui, keluar dari metode ini
        isUpdating = true;

        // Mendapatkan tanggal yang dipilih dari JCalendar
        LocalDate selectedDate = tanggalAwalCalendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Atur bulan dan tahun pada JComboBox dan JSpinner sesuai dengan tanggal di JCalendar
        bulanComboBox.setSelectedIndex(selectedDate.getMonthValue() - 1); // Bulan dimulai dari 0 di JComboBox
        tahunSpinner.setValue(selectedDate.getYear()); // Set tahun pada tahunSpinner

        // Memperbarui jumlah hari dan status kabisat
        hitungJumlahHariDanKabisat();

        isUpdating = false;
    }

        public void hitungSelisihHari() {
        // Ambil tanggal awal dan tanggal akhir dari JCalendar
        LocalDate tanggalAwal = tanggalAwalCalendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate tanggalAkhir = tanggalAkhirCalendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Hitung selisih hari
        long selisihHari = ChronoUnit.DAYS.between(tanggalAwal, tanggalAkhir);

        // Perbarui label selisih hari
        selisihHariLabel.setText("Selisih Hari: " + selisihHari + " hari");
    }
        
        
        
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        bulanComboBox = new javax.swing.JComboBox<>();
        tanggalAwalCalendar = new com.toedter.calendar.JCalendar();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        tanggalAkhirCalendar = new com.toedter.calendar.JCalendar();
        tahunSpinner = new javax.swing.JSpinner();
        kabisatLabel = new javax.swing.JLabel();
        selisihHariLabel = new javax.swing.JLabel();
        hariPertamaLabel = new javax.swing.JLabel();
        hitungButton = new javax.swing.JButton();
        hariTerakhirLabel = new javax.swing.JLabel();
        hasilLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Pilih Bulan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(65, 63, 0, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Masukan Tahun");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(9, 39, 0, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        bulanComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));
        bulanComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bulanComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(62, 3, 0, 0);
        jPanel1.add(bulanComboBox, gridBagConstraints);

        tanggalAwalCalendar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tanggalAwalCalendarPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 11;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 3, 0, 0);
        jPanel1.add(tanggalAwalCalendar, gridBagConstraints);

        jLabel3.setText("Tanggal Awal :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 39, 0, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Tanggal Akhir :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 24;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 18, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 48;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 26;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(38, 10, 0, 53);
        jPanel1.add(tanggalAkhirCalendar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 0);
        jPanel1.add(tahunSpinner, gridBagConstraints);

        kabisatLabel.setText("Tahun Kabisat =");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 70;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 39, 0, 0);
        jPanel1.add(kabisatLabel, gridBagConstraints);

        selisihHariLabel.setText("Selisih Hari =");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.ipadx = 154;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 39, 128, 0);
        jPanel1.add(selisihHariLabel, gridBagConstraints);

        hariPertamaLabel.setText("Hari Pertama =");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 23;
        gridBagConstraints.ipadx = 52;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 19, 0, 0);
        jPanel1.add(hariPertamaLabel, gridBagConstraints);

        hitungButton.setText("Hitung");
        hitungButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitungButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 24;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 25;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 36;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 7, 0, 0);
        jPanel1.add(hitungButton, gridBagConstraints);

        hariTerakhirLabel.setText("Hari Terakhir =");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.ipadx = 45;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 19, 128, 0);
        jPanel1.add(hariTerakhirLabel, gridBagConstraints);

        hasilLabel.setText("Jumlah Hari =");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 39, 0, 0);
        jPanel1.add(hasilLabel, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bulanComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bulanComboBoxActionPerformed
        updateCalendarFromSpinnerComboBox();
        hitungJumlahHariDanKabisat();
    }//GEN-LAST:event_bulanComboBoxActionPerformed

    private void hitungButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hitungButtonActionPerformed
        // TODO add your handling code here:
         hitungJumlahHari();
    }//GEN-LAST:event_hitungButtonActionPerformed

    private void tanggalAwalCalendarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tanggalAwalCalendarPropertyChange
        // TODO add your handling code here:
        hitungJumlahHariDanKabisat();
    }//GEN-LAST:event_tanggalAwalCalendarPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AplikasiPerhitunganHari.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AplikasiPerhitunganHari.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AplikasiPerhitunganHari.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AplikasiPerhitunganHari.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AplikasiPerhitunganHari().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> bulanComboBox;
    private javax.swing.JLabel hariPertamaLabel;
    private javax.swing.JLabel hariTerakhirLabel;
    private javax.swing.JLabel hasilLabel;
    private javax.swing.JButton hitungButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel kabisatLabel;
    private javax.swing.JLabel selisihHariLabel;
    private javax.swing.JSpinner tahunSpinner;
    private com.toedter.calendar.JCalendar tanggalAkhirCalendar;
    private com.toedter.calendar.JCalendar tanggalAwalCalendar;
    // End of variables declaration//GEN-END:variables
}
