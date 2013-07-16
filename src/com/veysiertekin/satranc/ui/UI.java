package com.veysiertekin.satranc.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.veysiertekin.satranc.board.Board;
import com.veysiertekin.satranc.board.tiles.Knight;
import com.veysiertekin.satranc.board.tiles.Tile;
import com.veysiertekin.satranc.board.tiles.TileState;
import com.veysiertekin.satranc.util.ChessDiscoverer;

/**
 * @author veysiertekin
 */
public class UI extends JFrame {
	private static Logger logger = Logger.getLogger(UI.class.getName());

	private int mod;

	private DrawingHelper drawingHelper;
	private Graphics2D threadDrawerGraphics;
	public ChessDiscoverer chessDiscoverer;
	private java.awt.Panel drawingArea;

	private javax.swing.JComboBox isHeuristicActive;
	private javax.swing.JComboBox whichTile;
	private javax.swing.JMenuBar menuBar;
	private javax.swing.JMenu file;
	private javax.swing.JMenuItem exit;
	private javax.swing.JMenu aboutMenu;
	private javax.swing.JMenuItem about;
	private javax.swing.JMenu functions;
	private javax.swing.JMenuItem clear;
	private javax.swing.JMenuItem solveProblem;

	private javax.swing.JButton next;
	private javax.swing.JButton playPause;
	private javax.swing.JButton prev;
	private javax.swing.JPopupMenu.Separator seperator;

	private javax.swing.JLabel statusBar;
	private javax.swing.JLabel tileSelectionText;
	private static final long serialVersionUID = 2331602266948345139L;

	public UI(int boyut) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
		}

		initComponents();

		threadDrawerGraphics = (Graphics2D) drawingArea.getGraphics();
		deactiveButtons(0);

		chessDiscoverer = new ChessDiscoverer(boyut);

		drawingHelper = new DrawingHelper();
		drawingHelper.setPriority(10);
		drawingHelper.start();
	}

	private void about(java.awt.event.ActionEvent evt) {
		alertDialog("Veysi Ertekin - Mart / 2013");
	}

	private void alertDialog(String string) {
		setEnabled(false);
		final JDialog a_ = new JDialog();
		JPanel a = new JPanel();
		JLabel aa = new JLabel(string);
		JButton aaa = new JButton("Tamam");

		aaa.setBounds(50, 50, 100, 50);
		a.add(aa);
		a.add(aaa);
		aaa.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				setEnabled(true);
				a_.dispose();
			}
		});
		a_.add(a);
		a_.setSize(350, 100);
		a_.setVisible(true);
		a_.setLocation(this.getLocation().x - 100 + this.getWidth() / 2, this.getLocation().y + 100);
		a_.setResizable(false);
		a_.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}

	public void clean() {
		mod = 0;

		deactiveButtons(0);

		threadDrawerGraphics.clearRect(0, 0, drawingArea.getSize().width, drawingArea.getSize().height);
		chessDiscoverer = new ChessDiscoverer(chessDiscoverer.getBoard().getTilesStates().length);
		statusBar.setText("");
	}

	private void clean(java.awt.event.ActionEvent evt) {
		clean();
	}

	public void deactiveButtons(int i) {
		switch (i) {
		case 0:
			prev.setEnabled(false);
			next.setEnabled(false);
			playPause.setEnabled(false);
			break;
		case 1:
			prev.setEnabled(true);
			next.setEnabled(true);
			playPause.setEnabled(true);
			break;
		case 2:
			prev.setEnabled(false);
			next.setEnabled(true);
			playPause.setEnabled(true);
			break;
		case 3:
			prev.setEnabled(true);
			next.setEnabled(false);
			playPause.setEnabled(false);
			break;
		}
	}

	@Override
	public void dispose() {
		try {
			drawingHelper.interrupt();
			drawingHelper.join();
			super.dispose();
		} catch (InterruptedException ex) {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void drawShape(java.awt.event.MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();

		double YGenislik = drawingArea.getSize().height / (double) chessDiscoverer.getBoard().getTilesStates().length;
		double XGenislik = drawingArea.getSize().width / (double) chessDiscoverer.getBoard().getTilesStates().length;

		x = (int) ((int) x / XGenislik);
		y = (int) ((int) y / YGenislik);

		if (chessDiscoverer.getBoard().getTilesStates()[y][x] == TileState.__EMPTY__ || mod == 10) {
			switch (mod) {
			case -1:
				break;
			default:
				try {
					chessDiscoverer.getBoard().setTilesStates(Board.getEmptyTileStates(chessDiscoverer.getBoard().getTilesStates().length));
					chessDiscoverer.getBoard().getTilesStates()[y][x] = TileState._CURRENT_;

					Tile tile = (Tile) ((Class) Class.forName(((JComboboxItem) whichTile.getSelectedItem()).getId())).getConstructor().newInstance();
					tile.setPosition(x, y);
					tile.isHeuristicActive = Boolean.parseBoolean(((JComboboxItem) isHeuristicActive.getSelectedItem()).getId());

					chessDiscoverer.getBoard().setTile(tile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void exitActionPerformed(java.awt.event.ActionEvent evt) {
		this.dispose();
	}

	private void findSolution(java.awt.event.ActionEvent evt) {
		if (mod == -1 || chessDiscoverer.getBoard().getTile() == null) { // çözüm bulunduysa
			return;
		}

		try {
			mod = -1;
			showStep();

			final JDialog dialog = new JDialog();

			final Thread th = new Thread() {
				@Override
				public void run() {
					long start, end;
					start = System.currentTimeMillis();
					if (chessDiscoverer.findSolution()) {
						end = System.currentTimeMillis();
						System.gc();

						logger.info("Solution: " + Arrays.toString(chessDiscoverer.getSolution()));
						logger.info("Time Estimation: " + (end - start) + " millisecond(s)");
						alertDialog("Çözüm " + (end - start) + " milisaniyede bulundu.");

						showStep();
					}
					else {
						deactiveButtons(0);
						logger.warning("There is no solution!");
						alertDialog("Çözüm Bulunamadı!");
					}
					dialog.dispose();
				}
			};

			JPanel a = new JPanel();
			JButton aaa = new JButton("Durdur");
			aaa.setBounds(50, 50, 200, 50);
			a.add(aaa);
			aaa.addMouseListener(new java.awt.event.MouseAdapter() {
				@SuppressWarnings("deprecation")
				@Override
				public void mouseClicked(java.awt.event.MouseEvent evt) {
					th.stop();
					alertDialog("Arama işlemi başarıyla durduruldu!");
					clean();
					dialog.dispose();
				}
			});
			dialog.add(a);
			dialog.setSize(350, 70);
			dialog.setVisible(true);
			dialog.setLocation(this.getLocation().x - 100 + this.getWidth() / 2, this.getLocation().y + 100);
			dialog.setResizable(false);
			dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

			setEnabled(false);
			th.start();
		} catch (Exception e) {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initComponents() {

		drawingArea = new java.awt.Panel() {
			private static final long serialVersionUID = -656469038083634829L;

			@Override
			public void paint(Graphics g) {
				if (drawingHelper != null) {
					drawingHelper.intType = 1;
					drawingHelper.interrupt();
					drawingHelper.intType = 0;
				}
			}
		};
		prev = new javax.swing.JButton();
		next = new javax.swing.JButton();
		playPause = new javax.swing.JButton();
		statusBar = new javax.swing.JLabel();
		tileSelectionText = new javax.swing.JLabel();
		whichTile = new javax.swing.JComboBox(new Vector() {
			private static final long serialVersionUID = 9004038530765628146L;
			{
				addElement(new JComboboxItem(Knight.class.getName(), "At"));
			}
		});
		isHeuristicActive = new javax.swing.JComboBox(new Vector() {
			private static final long serialVersionUID = 9004038530765628146L;
			{
				addElement(new JComboboxItem("true", "Bilgili Arama Açık"));
				addElement(new JComboboxItem("false", "Bilgili Arama Kapalı"));
			}
		});
		menuBar = new javax.swing.JMenuBar();
		file = new javax.swing.JMenu();
		exit = new javax.swing.JMenuItem();
		functions = new javax.swing.JMenu();
		clear = new javax.swing.JMenuItem();
		seperator = new javax.swing.JPopupMenu.Separator();
		solveProblem = new javax.swing.JMenuItem();
		aboutMenu = new javax.swing.JMenu();
		about = new javax.swing.JMenuItem();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Satranç: Bir Fetih Meselesi");
		setLocationByPlatform(true);
		setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);

		drawingArea.setBackground(new java.awt.Color(255, 254, 254));
		drawingArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		drawingArea.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				drawShape(evt);
			}
		});
		drawingArea.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseDragged(java.awt.event.MouseEvent evt) {
				drawShape(evt);
			}
		});

		javax.swing.GroupLayout CizimSahasiLayout = new javax.swing.GroupLayout(drawingArea);
		drawingArea.setLayout(CizimSahasiLayout);
		CizimSahasiLayout.setHorizontalGroup(CizimSahasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 0, Short.MAX_VALUE));
		CizimSahasiLayout.setVerticalGroup(CizimSahasiLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 572, Short.MAX_VALUE));

		prev.setFont(new java.awt.Font("Consolas", 1, 24)); // NOI18N
		prev.setText("<");
		prev.setToolTipText("Önceki");
		prev.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				prevActionPerformed(evt);
			}
		});

		next.setFont(new java.awt.Font("Consolas", 1, 24)); // NOI18N
		next.setText(">");
		next.setToolTipText("Sonraki");
		next.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				nextActionPerformed(evt);
			}
		});

		playPause.setFont(new java.awt.Font("Dialog", 1, 30)); // NOI18N
		playPause.setText("\u25B8");
		playPause.setToolTipText("Sonraki");
		playPause.addActionListener(new java.awt.event.ActionListener() {
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			Runnable periodicTask;
			Future<?> future;
			boolean aktif = true;

			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if(!check()) {
					if (periodicTask == null) periodicTask = new Runnable() {
						public void run() {
							if (chessDiscoverer.getSolution()[chessDiscoverer.getSolution().length - 1] != -1 && chessDiscoverer.getSolutionStep() != chessDiscoverer.getSolution().length - 1) {
								chessDiscoverer.setSolutionStep(chessDiscoverer.getSolutionStep() + 1);
								showStep();
							} else {
								check();
							}
						}
					};
					future = executor.scheduleAtFixedRate(periodicTask, 0, 450, TimeUnit.MILLISECONDS);
				}
			}

			boolean check(){
				aktif = !aktif;
				playPause.setText(!aktif ? "\u25e9" : "\u25b8");

				if(aktif)
					future.cancel(aktif);
				
				return aktif;
			}
		});

		statusBar.setBackground(new java.awt.Color(222, 221, 221));
		statusBar.setFont(new java.awt.Font("Consolas", 1, 11)); // NOI18N
		statusBar.setToolTipText("Durum çubuğu");

		tileSelectionText.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
		tileSelectionText.setText("Hangi taş ile yerleştirme yapılacak?");

		whichTile.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
		whichTile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					chessDiscoverer.getBoard().setTile((Tile) ((Class) Class.forName(((JComboboxItem) whichTile.getSelectedItem()).getId())).getConstructor().newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		isHeuristicActive.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
		isHeuristicActive.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Tile tile = chessDiscoverer.getBoard().getTile();
				if (tile != null) {
					tile.isHeuristicActive = Boolean.parseBoolean(((JComboboxItem) isHeuristicActive.getSelectedItem()).getId());
				}
			}
		});

		file.setText("Dosya");

		exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
		exit.setText("Çıkış");
		exit.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitActionPerformed(evt);
			}
		});
		file.add(exit);

		menuBar.add(file);
		file.getAccessibleContext().setAccessibleDescription("Dosya İşlemleri");

		functions.setText("İşlevler");

		clear.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
		clear.setText("Temizle");
		clear.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				clean(evt);
			}
		});
		functions.add(clear);
		functions.add(seperator);

		solveProblem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
		solveProblem.setText("Çözüm Bul");
		solveProblem.setToolTipText("Çözüm Bul");
		solveProblem.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				findSolution(evt);
			}
		});
		functions.add(solveProblem);

		menuBar.add(functions);

		aboutMenu.setText("    ?    ");

		about.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
		about.setText("Hakkında");
		about.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				about(evt);
			}
		});
		aboutMenu.add(about);

		menuBar.add(aboutMenu);

		setJMenuBar(menuBar);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(
												layout.createSequentialGroup().addComponent(drawingArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addContainerGap())
										.addGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addGap(0, 364, Short.MAX_VALUE)
														.addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap())
										.addGroup(
												javax.swing.GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup().addGap(8, 8, 8).addComponent(tileSelectionText).addGap(18, 18, 18)
														.addComponent(whichTile, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)
														.addComponent(isHeuristicActive, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(prev, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(playPause, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(next, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(25, 25, 25)))));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
										.addComponent(whichTile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(isHeuristicActive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(next, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tileSelectionText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(playPause, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tileSelectionText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(prev, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(drawingArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGap(2, 2, 2)
						.addComponent(statusBar, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)));

		drawingArea.getAccessibleContext().setAccessibleName("CizimSahasi");
		drawingArea.getAccessibleContext().setAccessibleDescription("");

		pack();
	}

	private void nextActionPerformed(java.awt.event.ActionEvent evt) {
		chessDiscoverer.setSolutionStep(chessDiscoverer.getSolutionStep() + 1);
		showStep();
	}

	private void prevActionPerformed(java.awt.event.ActionEvent evt) {
		chessDiscoverer.setSolutionStep(chessDiscoverer.getSolutionStep() - 1);
		showStep();
	}

	private void showStep() {
		int[] solution = chessDiscoverer.getSolution();
		if (solution[solution.length - 1] == -1) {
			deactiveButtons(0);
			statusBar.setText("");
		}
		else {
			if (chessDiscoverer.getSolutionStep() == 0) {
				deactiveButtons(2);
			}
			else if (chessDiscoverer.getSolutionStep() == solution.length - 1) {
				deactiveButtons(3);
			}
			else {
				deactiveButtons(1);
			}
			chessDiscoverer.getBoard().setTilesStates(Board.getPositionalBoardState(chessDiscoverer.getBoard().getTile(), chessDiscoverer.getSolution(), chessDiscoverer.getSolutionStep()));
			statusBar.setText((chessDiscoverer.getSolutionStep() + 1) + ". Adım");
		}
	}

	class DrawingHelper extends Thread {
		public int con = 0;
		public int intType = 0;

		@Override
		public void interrupt() {
			if (intType == 0) con++;
		}

		@Override
		public void run() {
			while (con == 0) {
				if (drawingArea.getWidth() == 0 || drawingArea.getHeight() == 0) continue;
				BufferedImage img = new BufferedImage(drawingArea.getWidth(), drawingArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D threadDrawerTMP = (Graphics2D) img.getGraphics();

				threadDrawerTMP.setColor(Color.white);
				threadDrawerTMP.fillRect(0, 0, drawingArea.getSize().width, drawingArea.getSize().height);
				try {
					/* +++ Dikey ve Yatay Çizgiler */
					int sliceSize = chessDiscoverer.getBoard().getTilesStates().length;

					double YWidth = drawingArea.getSize().height / (double) sliceSize;
					double XWidth = drawingArea.getSize().width / (double) sliceSize;

					threadDrawerTMP.setColor(Color.black);

					for (int i = 0; i <= sliceSize; i++) {
						if (i == 0) {
							threadDrawerTMP.drawLine(0, (int) (YWidth * i), drawingArea.getSize().width, (int) (YWidth * i));
						}
						else {
							threadDrawerTMP.drawLine(0, (int) (YWidth * i - 1), drawingArea.getSize().width, (int) (YWidth * i - 1));
						}
					}

					for (int i = 0; i <= sliceSize; i++) {
						if (i == 0) {
							threadDrawerTMP.drawLine((int) (XWidth * i), 0, (int) (XWidth * i), drawingArea.getSize().height);
						}
						else {
							threadDrawerTMP.drawLine((int) (XWidth * i - 1), 0, (int) (XWidth * i - 1), drawingArea.getSize().height);
						}
					}

					for (int x = 0; x <= sliceSize; x++) {
						for (int y = 0; y <= sliceSize; y++) {
							if (((x + y) & 1) == 1) {
								threadDrawerTMP.setColor(Color.BLACK);
								threadDrawerTMP.fillRect((int) (XWidth * x), (int) (YWidth * y), (int) XWidth, (int) YWidth);
							}
						}
					}
					/*--- Dikey ve Yatay Çizgiler */

					for (int j = 0; j < sliceSize; j++) { // Xs
						for (int i = 0; i < sliceSize; i++) { // Ys
							switch (chessDiscoverer.getBoard().getTilesStates()[j][i]) {
							case AVAILABLE:
								threadDrawerTMP.setColor(Color.GREEN);
								threadDrawerTMP.fillRect((int) (XWidth * i + XWidth * 3 / 16), (int) (YWidth * j + YWidth * 3 / 16), (int) (XWidth * 5 / 8), (int) (YWidth * 5 / 8));
								threadDrawerTMP.setColor(Color.BLACK);
								threadDrawerTMP.drawRect((int) (XWidth * i + XWidth * 3 / 16), (int) (YWidth * j + YWidth * 3 / 16), (int) (XWidth * 5 / 8), (int) (YWidth * 5 / 8));

								break;
							case _CURRENT_:
								if (chessDiscoverer.getBoard().getTile() != null) {
									Image image = chessDiscoverer.getBoard().getTile().getIcon();
									if (image != null) threadDrawerTMP.drawImage(image, (int) XWidth * i + (((int) XWidth - image.getWidth(null)) / 2),
											(int) YWidth * j + (((int) YWidth - image.getHeight(null)) / 2), null);
								}
								break;
							case NOT_EMPTY:
								threadDrawerTMP.setColor(Color.RED);
								threadDrawerTMP.fillRect((int) (XWidth * i + XWidth * 3 / 16), (int) (YWidth * j + YWidth * 3 / 16), (int) (XWidth * 5 / 8), (int) (YWidth * 5 / 8));
								threadDrawerTMP.setColor(Color.BLACK);
								threadDrawerTMP.drawRect((int) (XWidth * i + XWidth * 3 / 16), (int) (YWidth * j + YWidth * 3 / 16), (int) (XWidth * 5 / 8), (int) (YWidth * 5 / 8));

								break;
							case __EMPTY__:
								break;
							default:
							}
						}
					}
					{
						Stroke s = threadDrawerTMP.getStroke();
						threadDrawerTMP.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

						int[] solution = chessDiscoverer.getSolution();
						int step = chessDiscoverer.getSolutionStep();

						threadDrawerTMP.setColor(new Color(100,100, 255, 255));
						for (int i = 0; i < step;) {
							if (i == step - 1) threadDrawerTMP.setColor(Color.YELLOW);

							int size = chessDiscoverer.getBoard().getTilesStates().length;

							int[] p1 = Board.decodeLocation(solution[++i], size);
							int[] p2 = Board.decodeLocation(solution[i - 1], size);

							threadDrawerTMP
									.drawLine((int) (p1[0] * XWidth + XWidth / 2), (int) (p1[1] * YWidth + YWidth / 2), (int) (p2[0] * XWidth + XWidth / 2), (int) (p2[1] * YWidth + YWidth / 2));//(new Line2D.Double(p1[0], p1[1], p2[0], p2[1]));

						}
						threadDrawerTMP.setStroke(s);
					}

					UI.this.threadDrawerGraphics.drawImage(img, 0, 0, null);
					Thread.sleep(200);
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	class JComboboxItem {
		private String description;
		private String id;

		public JComboboxItem(String id, String description) {
			this.id = id;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toString() {
			return description;
		}
	}
}
