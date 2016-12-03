/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package setnotesdesktop;

import java.awt.Container;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jdk.nashorn.internal.runtime.Debug;
import setnotesclient.Note;

/**
 *
 * @author Adam
 */
public class NoteJPanel extends javax.swing.JPanel{
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private MainNotesJFrame mainFrame;
    private Note note;

    /**
     * Creates new form NoteJPanel
     */
    public NoteJPanel(MainNotesJFrame mainFrame){
        initComponents();
        this.mainFrame = mainFrame;
        
        note = new Note();
        createdLabel.setText("created: " + dateFormat.format(note.getCreateDate()));
        editedLabel.setText("");
        
        confirmPanel.setVisible(true);
        saveButton.setVisible(false);
        
        noteTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e){
                noteTextChanged();
            }
            @Override
            public void removeUpdate(DocumentEvent e){
                noteTextChanged();
            }
            @Override
            public void changedUpdate(DocumentEvent e){
                noteTextChanged();
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        noteTextArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        deleteButton = new javax.swing.JButton();
        createdLabel = new javax.swing.JLabel();
        editedLabel = new javax.swing.JLabel();
        confirmPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        setMaximumSize(new java.awt.Dimension(99999, 190));
        setMinimumSize(new java.awt.Dimension(300, 190));
        setPreferredSize(new java.awt.Dimension(300, 190));

        noteTextArea.setColumns(20);
        noteTextArea.setRows(5);
        jScrollPane1.setViewportView(noteTextArea);

        jPanel1.setPreferredSize(new java.awt.Dimension(32, 32));

        deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/setnotesdesktop/img/xbutton.png"))); // NOI18N
        deleteButton.setToolTipText("delete");
        deleteButton.setBorder(null);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setPreferredSize(new java.awt.Dimension(32, 32));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        createdLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        createdLabel.setText("created: 2016-9-26");

        editedLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        editedLabel.setText("edited: 2016-8-26");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(createdLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(editedLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(createdLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editedLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        confirmPanel.setPreferredSize(new java.awt.Dimension(73, 32));

        cancelButton.setText("cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(73, 25));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        saveButton.setText("save");
        saveButton.setPreferredSize(new java.awt.Dimension(73, 25));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout confirmPanelLayout = new javax.swing.GroupLayout(confirmPanel);
        confirmPanel.setLayout(confirmPanelLayout);
        confirmPanelLayout.setHorizontalGroup(
            confirmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, confirmPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        confirmPanelLayout.setVerticalGroup(
            confirmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(confirmPanelLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(confirmPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 4, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(confirmPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(confirmPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        //todo: confirm, with dont show again checkbox
        mainFrame.getNoteStore().delete(note);
        Container parent = getParent();
        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (note.getNoteBody().isEmpty()) {
            //remove component because not was never created
            Container parent = getParent();
            parent.remove(this);
            parent.revalidate();
            parent.repaint();
        }
        
        confirmPanel.setVisible(false);
        noteTextArea.setText(note.getNoteBody());
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        note.setNoteBody(noteTextArea.getText());
        editedLabel.setText("edited: " + dateFormat.format(note.getEditDate()));
        mainFrame.getNoteStore().addOrUpdate(note);
        confirmPanel.setVisible(false);
    }//GEN-LAST:event_saveButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel confirmPanel;
    private javax.swing.JLabel createdLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JLabel editedLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea noteTextArea;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
    
    private void noteTextChanged(){
        String text = noteTextArea.getText();
        
        if(text.equals(note.getNoteBody())){
            if(text.equals("")){
                //keep cancel but hide save
                confirmPanel.setVisible(true);
                saveButton.setVisible(false);
            }else{
                //hide confirm panel
                confirmPanel.setVisible(false);
            }
        }else{
            //show confirm panel
            confirmPanel.setVisible(true);
            saveButton.setVisible(true);
        }
    }
    
}