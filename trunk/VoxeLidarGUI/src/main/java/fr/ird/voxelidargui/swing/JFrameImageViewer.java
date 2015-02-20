/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.ird.voxelidargui.swing;

import fr.ird.voxelidar.util.image.ImageUtility;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Julien
 */
public class JFrameImageViewer extends javax.swing.JFrame{

    /**
     * Creates new form JFrameImageViewer
     */
    
    BufferedImage image;
    BufferedImage colorScaleImage;
    Point oldMousePosition;
    Point newMousePosition;
    final PicturePanel picturePanelImage;
    
    public JFrameImageViewer(BufferedImage image, BufferedImage colorScaleImage) {
        initComponents();
        
        postInit();
        
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        this.image = image;
        this.colorScaleImage = colorScaleImage;
        
        newMousePosition = new Point(jPanelImage.getWidth()/2, jPanelImage.getHeight()/2);
        oldMousePosition = new Point(0, 0);
        
        picturePanelImage = new PicturePanel(image, image.getWidth(), image.getHeight(), jPanelImage.getWidth()/2, jPanelImage.getHeight()/2);
        picturePanelImage.setLocation(jPanelImage.getWidth()/2, jPanelImage.getHeight()/2);
        
        PicturePanel picturePanelColorScaleImage = new PicturePanel(colorScaleImage, colorScaleImage.getWidth()*2, colorScaleImage.getHeight()*2, 0, 0);

        jPanelImage.add(picturePanelImage, BorderLayout.CENTER);
        jPanelImage.revalidate();
        
        jPanelScale.add(picturePanelColorScaleImage, BorderLayout.CENTER);
        jPanelScale.revalidate();
    }
    
    private void postInit(){
        
        FileFilter jpegFilter= new FileNameExtensionFilter("JPEG file", "jpg");
        jFileChooserSaveImage.addChoosableFileFilter(jpegFilter);
        
        FileFilter pngFilter= new FileNameExtensionFilter("PNG file", "png");
        jFileChooserSaveImage.addChoosableFileFilter(pngFilter);
        
        FileFilter gifFilter= new FileNameExtensionFilter("GIF file", "gif");
        jFileChooserSaveImage.addChoosableFileFilter(gifFilter);
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooserSaveImage = new javax.swing.JFileChooser();
        jPanelScale = new javax.swing.JPanel();
        jPanelImage = new javax.swing.JPanel();
        jPanelInfos = new javax.swing.JPanel();
        jLabelMinValue = new javax.swing.JLabel();
        jLabelMaxValue = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        jFileChooserSaveImage.setAcceptAllFileFilterUsed(false);
        jFileChooserSaveImage.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanelScale.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelScale.setPreferredSize(new java.awt.Dimension(100, 400));
        jPanelScale.setLayout(new java.awt.CardLayout());

        jPanelImage.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanelImage.setPreferredSize(new java.awt.Dimension(574, 200));
        jPanelImage.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jPanelImageMouseWheelMoved(evt);
            }
        });
        jPanelImage.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jPanelImageMouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanelImageMouseMoved(evt);
            }
        });
        jPanelImage.setLayout(new java.awt.BorderLayout());

        jPanelInfos.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabelMinValue.setBorder(javax.swing.BorderFactory.createTitledBorder("Min"));
        jLabelMinValue.setMaximumSize(new java.awt.Dimension(10, 0));
        jLabelMinValue.setPreferredSize(new java.awt.Dimension(100, 22));

        jLabelMaxValue.setBorder(javax.swing.BorderFactory.createTitledBorder("Max"));
        jLabelMaxValue.setMaximumSize(new java.awt.Dimension(10, 0));
        jLabelMaxValue.setPreferredSize(new java.awt.Dimension(100, 22));

        javax.swing.GroupLayout jPanelInfosLayout = new javax.swing.GroupLayout(jPanelInfos);
        jPanelInfos.setLayout(jPanelInfosLayout);
        jPanelInfosLayout.setHorizontalGroup(
            jPanelInfosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabelMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabelMinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanelInfosLayout.setVerticalGroup(
            jPanelInfosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelInfosLayout.createSequentialGroup()
                .addComponent(jLabelMaxValue, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelMinValue, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addContainerGap())
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Save image");
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Save image as...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanelScale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanelInfos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelImage, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelImage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanelScale, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelInfos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanelImageMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jPanelImageMouseWheelMoved
        
        PicturePanel p = (PicturePanel) jPanelImage.getComponent(0);
        
        if(evt.getWheelRotation()<0 && p.width < (Integer.MAX_VALUE/2) && p.height < (Integer.MAX_VALUE/2)){
            
            p.posX -= newMousePosition.x-p.posX;
            p.posY -= newMousePosition.y-p.posY;
            
            p.width *= 2;
            p.height *= 2;
            
        }else if(evt.getWheelRotation()>0 && p.width/2 >= p.originalWidth && p.height/2 >= p.originalHeight){
            
            p.posX += (newMousePosition.x - p.posX)/2;
            p.posY += (newMousePosition.y - p.posY)/2;
            
            p.width /= 2;
            p.height /= 2;
        }
        
        jPanelImage.revalidate();
        jPanelImage.repaint();
        
    }//GEN-LAST:event_jPanelImageMouseWheelMoved

    private void jPanelImageMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelImageMouseMoved
        
        oldMousePosition = new Point(newMousePosition.x, newMousePosition.y);
        newMousePosition = new Point(evt.getX(), evt.getY());
        
        
    }//GEN-LAST:event_jPanelImageMouseMoved

    private void jPanelImageMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelImageMouseDragged
        
        oldMousePosition = new Point(newMousePosition.x, newMousePosition.y);
        newMousePosition = new Point(evt.getX(), evt.getY());
        
        picturePanelImage.posX += (newMousePosition.x - oldMousePosition.x);
        picturePanelImage.posY += (newMousePosition.y - oldMousePosition.y);

        jPanelImage.repaint();
    }//GEN-LAST:event_jPanelImageMouseDragged

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        
        if(jFileChooserSaveImage.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
            
            FileNameExtensionFilter filter = (FileNameExtensionFilter) jFileChooserSaveImage.getFileFilter();
            String[] extensions = filter.getExtensions();
            
            switch(extensions[0]){
                case "png":
                    ImageUtility.saveImage(image, ImageUtility.Format.PNG, jFileChooserSaveImage.getSelectedFile().getAbsolutePath());
                    break;
                case "jpg":
                    ImageUtility.saveImage(image, ImageUtility.Format.JPG, jFileChooserSaveImage.getSelectedFile().getAbsolutePath());
                    break;
                case "gif":
                    ImageUtility.saveImage(image, ImageUtility.Format.GIF, jFileChooserSaveImage.getSelectedFile().getAbsolutePath());
                    break;
            }
            
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    public void setJLabelMaxValue(String maxValue){
        jLabelMaxValue.setText(maxValue);
        jLabelMaxValue.setToolTipText(maxValue);
    }
    
    public void setJLabelMinValue(String minValue){
        jLabelMinValue.setText(minValue);
        jLabelMinValue.setToolTipText(minValue);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser jFileChooserSaveImage;
    private javax.swing.JLabel jLabelMaxValue;
    private javax.swing.JLabel jLabelMinValue;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanelImage;
    private javax.swing.JPanel jPanelInfos;
    private javax.swing.JPanel jPanelScale;
    // End of variables declaration//GEN-END:variables

}
