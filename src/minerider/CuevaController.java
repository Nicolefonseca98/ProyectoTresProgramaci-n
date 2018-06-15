package minerider;

import animación.AnimaciónCueva;
import animación.AnimaciónPersonaje;
import animación.AnimaciónQuimera;
import animación.AnimaciónZombie;
import dominio.Quimera;
import dominio.Zombie;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;

/**
 * FXML Controller class
 *
 * @author Nicole Fonseca, Wilmer Mata
 */
public class CuevaController implements Initializable {

    @FXML
    private StackPane stackPane;
    @FXML
    private ImageView background1;
    @FXML
    private ImageView corazon1;
    @FXML
    private ImageView corazon2;
    @FXML
    private ImageView corazon3;
    private Image cueva;
    private Image corazonLleno;
    private Image corazonVacio;
    private Canvas canvas;
    private AnimaciónZombie animaciónZombie;
    private AnimaciónQuimera animaciónQuimera;
    private AnimaciónPersonaje animaciónPersonaje;
    private AnimaciónCueva animaciónCueva;
    private Area personaje;
    private int corazonPersonaje = 0;
    private int vidasQuimera = 0;
    private int vidasZombie = 0;
    GraphicsContext graphicsContext;
    static ArrayList<Quimera> arrayListQuimera = new ArrayList<>();
    static ArrayList<Zombie> arrayListZombie = new ArrayList<>();
    
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
                    colisión();
                    Thread.sleep(tiempoEspera);
                    graphicsContext = this.canvas.getGraphicsContext2D();
                    dibujarPersonajes(graphicsContext);
                } catch (InterruptedException ex) {
                    System.out.println("Exception");
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CuevaController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CuevaController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        Thread hilo = new Thread(runnable);
        hilo.start();
    }

    //Dibuja imagénes 
    private void dibujarPersonajes(GraphicsContext graphicsContext) {
        graphicsContext.clearRect(0, 0, 813, 400);
        for (int i = 0; i < arrayListZombie.size(); i++) {
            Zombie zombieAux = arrayListZombie.get(i);
            graphicsContext.drawImage(zombieAux.getImage(), zombieAux.getX(), zombieAux.getY());
        }

        for (int i = 0; i < arrayListQuimera.size(); i++) {
            Quimera quimeraAux = arrayListQuimera.get(i);
            graphicsContext.drawImage(quimeraAux.getImage(), quimeraAux.getX(), quimeraAux.getY());
        }
        graphicsContext.drawImage(this.animaciónPersonaje.getImage(), this.animaciónPersonaje.getX(), animaciónPersonaje.getY());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //Audio
//        AudioClip note = new AudioClip(this.getClass().getResource("/music/contra.mp3").toString());
//        note.play();

        //Imagen de fondo
        cueva = new Image("/cueva/cueva1.png");
        background1.setImage(cueva);
        corazonLleno = new Image("/starlord/heart.png");
        corazon1.setImage(corazonLleno);
        corazon2.setImage(corazonLleno);
        corazon3.setImage(corazonLleno);

        //Personajes
        try {
            int x = 600;
            for (int i = 0; i < 2; i++) {
                animaciónQuimera = new AnimaciónQuimera(x, 310);
                Thread threadQuimera = new Thread(animaciónQuimera);
                threadQuimera.setName("Quimera "+i);
                threadQuimera.start();
                arrayListQuimera.add(animaciónQuimera);
                x += 350;
                System.out.println(threadQuimera.getName());
            }
            
            int xZombie = -300;
            for (int i = 0; i < 2; i++) {
                animaciónZombie = new AnimaciónZombie(xZombie, 300);
                Thread threadZombie = new Thread(animaciónZombie);
                threadZombie.setName("Zombie "+i);
                threadZombie.start();
                arrayListZombie.add(animaciónZombie);
                xZombie += 350;
            }
            animaciónPersonaje = new AnimaciónPersonaje(50, 0);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(CuevaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Canvas
        this.canvas = new Canvas(813, 400);
        stackPane.getChildren().add(canvas);

        //Hilo principal
        run();
        animaciónPersonaje.movimientPersonaje(stackPane, 310);
    }

    /**
     * Area en que se encuentra el personaje principal.
     *
     * @return Area
     */
    public Area getBounds() {
        Rectangle starlord = new Rectangle(animaciónPersonaje.getX(), animaciónPersonaje.getY(), 34, 36);
        personaje = new Area(starlord);

        return personaje;
    }

    /**
     * Detecta si hay una colisión entre el personaje y algún monstruo.
     *
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    public void colisión() throws FileNotFoundException, InterruptedException, IOException {

        Boolean obstaculo = false;
        corazonLleno = new Image("/starlord/heart.png");
        corazonVacio = new Image("/starlord/emptyHeart.png");

        for (int i = 0; i < arrayListQuimera.size(); i++) {
            Quimera quimeraAux = arrayListQuimera.get(i);
            Rectangle quimera = new Rectangle(quimeraAux.getX(), quimeraAux.getY(), 38, 40);
            Area areaQuimera = new Area(quimera);
            areaQuimera.intersect(getBounds());
            if (!areaQuimera.isEmpty()) {
                obstaculo = true;
            }
        }
        for (int i = 0; i < arrayListZombie.size(); i++) {
            Area areaZombie = null;
            Zombie zombieAux = arrayListZombie.get(i);
            Rectangle zombie = new Rectangle(zombieAux.getX(), zombieAux.getY(), 38, 40);
            areaZombie = new Area(zombie);
            areaZombie.intersect(getBounds());
            if (!areaZombie.isEmpty()) {
                obstaculo = true;
            }
        }

        if (obstaculo == true) {
            
            if (animaciónQuimera.llamarada() == true || animaciónZombie.muerdeCerebro() == true) {
                corazonPersonaje++;
                  switch (corazonPersonaje) {
            case 0:
                corazon1.setImage(corazonLleno);
                corazon2.setImage(corazonLleno);
                corazon3.setImage(corazonLleno);
                break;
            case 5:
                corazon1.setImage(corazonLleno);
                corazon2.setImage(corazonLleno);
                corazon3.setImage(corazonVacio);
                break;
            case 17:
                corazon1.setImage(corazonLleno);
                corazon2.setImage(corazonVacio);
                corazon3.setImage(corazonVacio);
                break;
            case 28:
                corazon1.setImage(corazonVacio);
                corazon2.setImage(corazonVacio);
                corazon3.setImage(corazonVacio);
                System.out.println("¡¡¡¡¡¡Juego terminado!!!!!");
                break;
        }
                System.out.println("Rawr");
            } else if (animaciónPersonaje.arma() == 1) {
                vidasQuimera++;
                switch(vidasQuimera) {
                    case 0:
                        System.out.println("3 vidas");
                        break;
                    case 5:
                        System.out.println("2 vidas");
                        break;
                    case 8:
                        System.out.println("1 vida");
                        break;
                    case 12:
//                        graphicsContext = this.canvas.getGraphicsContext2D();
//                        graphicsContext.clearRect(this.animaciónQuimera.getX(), this.animaciónQuimera.getY(), 38, 40);
                        System.out.println("*************Murió quimera******************");
                        break;
                }
                System.out.println("Ataco quimera");
            } else if (animaciónPersonaje.arma() == 2) {
                System.out.println("Latigazo");
            } else if (animaciónPersonaje.arma() == 3) {
                System.out.println("Palazo");
            } else {
                System.out.println("Desarmado");
            }
        }
    }
    
}
