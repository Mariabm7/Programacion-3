

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;
import java.util.Arrays;

import javax.swing.*;

import com.sun.org.apache.bcel.internal.generic.LMUL;

/** Clase principal de minijuego de coche para Pr�ctica 02 - Prog III
 * Ventana del minijuego.
 * @author Andoni Egu�luz
 * Facultad de Ingenier�a - Universidad de Deusto (2014)
 */
public class VentanaJuego extends JFrame {
	private static final long serialVersionUID = 1L;  // Para serializaci�n
	JPanel pPrincipal;         // Panel del juego (layout nulo)
	MundoJuego miMundo;        // Mundo del juego 
	CocheJuego miCoche;        // Coche del juego
	MiRunnable miHilo = null;  // Hilo del bucle principal de juego	
	
	private Boolean [] pulsaciones = new Boolean[4];
	private JLabel lMensaje;
	
	/** Constructor de la ventana de juego. Crea y devuelve la ventana inicializada
	 * sin coches dentro
	 */
	public VentanaJuego() {
		Arrays.fill(pulsaciones, Boolean.FALSE);
		// Liberaci�n de la ventana por defecto al cerrar
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		// Creaci�n contenedores y componentes
		pPrincipal = new JPanel();
		JPanel pBotonera = new JPanel();
		JButton bAcelerar = new JButton( "Acelera" );
		JButton bFrenar = new JButton( "Frena" );
		JButton bGiraIzq = new JButton( "Gira Izq." );
		JButton bGiraDer = new JButton( "Gira Der." );
		lMensaje = new JLabel("Puntos: 0hjkh - ESTRELLAS PERDIDAS: 0");
		// Formato y layouts
		pPrincipal.setLayout( null );
		pPrincipal.setBackground( Color.white );
		// A�adido de componentes a contenedores
		add( pPrincipal, BorderLayout.CENTER );
//		pBotonera.add( bAcelerar );
//		pBotonera.add( bFrenar );
//		pBotonera.add( bGiraIzq );
//		pBotonera.add( bGiraDer );
		pBotonera.add(lMensaje);
		
		add( pBotonera, BorderLayout.SOUTH );
		// Formato de ventana
		setSize( 1000, 750 );
		setResizable( false );
		// Escuchadores de botones
		bAcelerar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.acelera( +10, 1 );
				// System.out.println( "Nueva velocidad de coche: " + miCoche.getVelocidad() );
			}
		});
		bFrenar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.acelera( -10, 1 );
				// System.out.println( "Nueva velocidad de coche: " + miCoche.getVelocidad() );
			}
		});
		bGiraIzq.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.gira( +10 );
				// System.out.println( "Nueva direcci�n de coche: " + miCoche.getDireccionActual() );
			}
		});
		bGiraDer.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				miCoche.gira( -10 );
				// System.out.println( "Nueva direcci�n de coche: " + miCoche.getDireccionActual() );
			}
		});
		
		// A�adido para que tambi�n se gestione por teclado con el KeyListener
		pPrincipal.addKeyListener( new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: {
					pulsaciones[0]= false;
					break;
				}
				case KeyEvent.VK_DOWN: {
					pulsaciones[1]= false;
					break;
				}
				case KeyEvent.VK_LEFT: {
					pulsaciones[2]= false;
					break;
				}
				case KeyEvent.VK_RIGHT: {
					pulsaciones[3]= false;
					break;
				}
			}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP: {
					pulsaciones[0]= true;
					break;
				}
				case KeyEvent.VK_DOWN: {
					pulsaciones[1]= true;
					break;
				}
				case KeyEvent.VK_LEFT: {
					pulsaciones[2]= true;
					break;
				}
				case KeyEvent.VK_RIGHT: {
					pulsaciones[3]= true;
					break;
				}
			}
//				switch (e.getKeyCode()) {
//					case KeyEvent.VK_UP: {
//						miCoche.acelera( +5, 1 );
//						break;
//					}
//					case KeyEvent.VK_DOWN: {
//						miCoche.acelera( -5, 1 );
//						break;
//					}
//					case KeyEvent.VK_LEFT: {
//						miCoche.gira( +10 );
//						break;
//					}
//					case KeyEvent.VK_RIGHT: {
//						miCoche.gira( -10 );
//						break;
//					}
//				}
			}
		});
		pPrincipal.setFocusable(true);
		pPrincipal.requestFocus();
		pPrincipal.addFocusListener( new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				pPrincipal.requestFocus();
			}
		});
		// Cierre del hilo al cierre de la ventana
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (miHilo!=null) miHilo.acaba();
			}
		});
	}
	
	/** Programa principal de la ventana de juego
	 * @param args
	 */
	public static void main(String[] args) {
		// Crea y visibiliza la ventana con el coche
		try {
			final VentanaJuego miVentana = new VentanaJuego();
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override
				public void run() {
					miVentana.setVisible( true );
				}
			});
			miVentana.miMundo = new MundoJuego( miVentana.pPrincipal );
			miVentana.miMundo.creaCoche( 150, 100 );
			miVentana.miCoche = miVentana.miMundo.getCoche();
			miVentana.miCoche.setPiloto( "Fernando Alonso" );
			
			miVentana.miMundo.creaEstrella();
			
			// Crea el hilo de movimiento del coche y lo lanza
			miVentana.miHilo = miVentana.new MiRunnable();  // Sintaxis de new para clase interna
			Thread nuevoHilo = new Thread( miVentana.miHilo );
			nuevoHilo.start();
		} catch (Exception e) {
			System.exit(1);  // Error anormal
		}
	}
	
	/** Clase interna para implementaci�n de bucle principal del juego como un hilo
	 * @author Andoni Egu�luz
	 * Facultad de Ingenier�a - Universidad de Deusto (2014)
	 */
	class MiRunnable implements Runnable {
		double fuerza;
		boolean sigo = true;
		@Override
		public void run() {
			// Bucle principal forever hasta que se pare el juego...
			while (sigo) {
				if(System.currentTimeMillis() - miMundo.getUltimaCreacion() >= 1200){
					miMundo.creaEstrella();
				}
				if (miMundo.quitaYRotaEstrellas(6000)){
					lMensaje.setText("Puntos "+ miMundo.getPuntuacion()+ "- ESTRELLAS PERDIDAS: "+ miMundo.getPerdidas());
				}
				if (miMundo.getPerdidas()>=10){
					sigo = false;
				}
				miMundo.choqueConEstrellas();
				fuerza = 0;
				
				
				if (pulsaciones[0]){
					//miCoche.acelera(+5, 1);
					fuerza = miCoche.fuerzaAceleracionAdelante();
				}else if (pulsaciones[1]){
					//miCoche.acelera(-5, -1);
					fuerza = miCoche.fuerzaAceleracionAtras();
				}
				MundoJuego.aplicarFuerza(fuerza, miCoche);
				
				if (pulsaciones[2]){
					miCoche.gira(+10);
				}else if (pulsaciones[3]){
					miCoche.gira(-10);
				}
				
				// Mover coche
				miCoche.mueve( 0.040 );
				// Chequear choques
				// (se comprueba tanto X como Y porque podr�a a la vez chocar en las dos direcciones (esquinas)
				if (miMundo.hayChoqueHorizontal(miCoche)) // Espejo horizontal si choca en X
					miMundo.rebotaHorizontal(miCoche);
				if (miMundo.hayChoqueVertical(miCoche)) // Espejo vertical si choca en Y
					miMundo.rebotaVertical(miCoche);
				// Dormir el hilo 40 milisegundos
				try {
					Thread.sleep( 40 );
				} catch (Exception e) {
				}
			}
		}
		/** Ordena al hilo detenerse en cuanto sea posible
		 */
		public void acaba() {
			sigo = false;
		}
	};
	
}
