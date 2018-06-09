
package minerider;

import animación.AnimaciónCueva;
import animación.AnimaciónPersonaje;
import animación.AnimaciónQuimera;
import animación.AnimaciónZombie;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;

/**
 * FXML Controller class
 *
 * @author Nicole Fonseca, Wilmer Mata
 */


public class CuevaController implements Initializable {
 
    @FXML private StackPane stackPane;
    @FXML private ImageView background1;
    private Image cueva1;
    private Canvas canvas;
    private AnimaciónZombie animaciónZombie;
    private AnimaciónZombie animaciónZombie1;
    private AnimaciónQuimera animaciónQuimera;
    private AnimaciónQuimera animaciónQuimera1;
    private AnimaciónPersonaje animaciónPersonaje;
    private AnimaciónCueva animaciónCueva;
    
    //Hilo principal
    public void run() {
        Runnable runnable = () -> {
            long inicio;
            long transcurrido;
            long tiempoEspera;
            int fps = 30;
            long tiempo = 1000 / fps;

            while (true) {
                try {
                    inicio = System.nanoTime();
                    transcurrido = System.nanoTime() - inicio;
                    tiempoEspera = tiempo - transcurrido / 1000000;
                    prueba();
                    Thread.sleep(tiempoEspera);
                    
                    GraphicsContext graphicsContext = this.canvas.getGraphicsContext2D();
                    draw(graphicsContext);
                } catch (InterruptedException ex) {
                    System.out.println("Exception");
                }
            }
        };
        Thread hilo = new Thread(runnable);
        hilo.start();
    }
    
    //Dibuja imagénes 
    private void draw(GraphicsContext graphicsContext){
        graphicsContext.clearRect(0, 0, 813, 400);
        graphicsContext.drawImage(this.animaciónZombie.getImage(), this.animaciónZombie.getX(), this.animaciónZombie.getY());
        graphicsContext.drawImage(this.animaciónZombie1.getImage(), this.animaciónZombie1.getX(), this.animaciónZombie1.getY());
        graphicsContext.drawImage(this.animaciónQuimera.getImage(), this.animaciónQuimera.getX(), this.animaciónQuimera.getY());
//        graphicsContext.drawImage(this.animaciónQuimera1.getImage(), this.animaciónQuimera1.getX(), this.animaciónQuimera1.getY());
    }
   
   @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Audio
//        AudioClip note = new AudioClip(this.getClass().getResource("/music/contra.mp3").toString());
//        note.play();
        //Imagen de fondo
        cueva1 = new Image("/cueva/cueva1.png");
        background1.setImage(cueva1);
        
        //Personajes
        try {
            animaciónZombie = new AnimaciónZombie(100, 300);
            animaciónZombie1 = new AnimaciónZombie(100, 100);
            animaciónQuimera = new AnimaciónQuimera(300, 260);
//            animaciónQuimera1 = new AnimaciónQuimera(0,300);
            animaciónPersonaje = new AnimaciónPersonaje(0, 0);
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CuevaController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Canvas
        this.canvas = new Canvas(813, 400);
        stackPane.getChildren().add(canvas);
  
        //Movimiento personaje
        GridPane gridPane = animaciónPersonaje.hiloPersonaje(stackPane);
        stackPane.getChildren().add(gridPane);
//        gridPane = animaciónQuimera.hiloQuimera();
//        stackPane.getChildren().add(gridPane);
        
        //Hilos
        run();
        animaciónZombie.hiloZombie();
        animaciónZombie1.hiloZombie();
        animaciónQuimera.hiloQuimera();
//        animaciónQuimera1.hiloQuimera();
    }
    
    public void prueba() throws InterruptedException {
        int a = this.animaciónZombie.getX();
        int b = this.animaciónQuimera.getX();
        if (a == b) {
            System.out.println("chocaron");
        }
    }

}


