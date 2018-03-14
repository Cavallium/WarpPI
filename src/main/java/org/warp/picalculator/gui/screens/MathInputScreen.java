package org.warp.picalculator.gui.screens;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.warp.picalculator.Error;
import org.warp.picalculator.Errors;
import org.warp.picalculator.StaticVars;
import org.warp.picalculator.Utils;
import org.warp.picalculator.device.Keyboard;
import org.warp.picalculator.device.Keyboard.Key;
import org.warp.picalculator.gui.DisplayManager;
import org.warp.picalculator.gui.expression.InputContext;
import org.warp.picalculator.gui.expression.blocks.Block;
import org.warp.picalculator.gui.expression.blocks.BlockContainer;
import org.warp.picalculator.gui.expression.containers.InputContainer;
import org.warp.picalculator.gui.expression.containers.NormalInputContainer;
import org.warp.picalculator.gui.expression.containers.NormalOutputContainer;
import org.warp.picalculator.gui.expression.containers.OutputContainer;
import org.warp.picalculator.gui.graphicengine.BinaryFont;
import org.warp.picalculator.gui.graphicengine.Renderer;
import org.warp.picalculator.math.AngleMode;
import org.warp.picalculator.math.Function;
import org.warp.picalculator.math.FunctionDynamic;
import org.warp.picalculator.math.FunctionOperator;
import org.warp.picalculator.math.FunctionSingle;
import org.warp.picalculator.math.MathContext;
import org.warp.picalculator.math.MathematicalSymbols;
import org.warp.picalculator.math.functions.Expression;
import org.warp.picalculator.math.functions.Number;
import org.warp.picalculator.math.functions.Variable;
import org.warp.picalculator.math.functions.Variable.VariableValue;
import org.warp.picalculator.math.parser.MathParser;
import org.warp.picalculator.math.solver.MathSolver;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class MathInputScreen extends Screen {

	private static final BinaryFont fontBig = Utils.getFont(false);
	
	public MathContext calc;
	public InputContext ic;
	public InputContainer userInput;
	public OutputContainer result;
	public int errorLevel = 0; // 0 = nessuno, 1 = risultato, 2 = tutto
	private boolean computingResult = false;
	private Thread computingThread;
	private int computingAnimationIndex = 0;
	private double computingAnimationElapsedTime = 0;
	private double computingElapsedTime = 0;
	private boolean computingBreakTipVisible = false;
	boolean mustRefresh = true;
	private int currentStep = 0;

	public MathInputScreen() {
		super();
		canBeInHistory = true;
	}

	@Override
	public void created() throws InterruptedException {
		ic = new InputContext();
		calc = new MathContext();

		try {
			BlockContainer.initializeFonts(DisplayManager.INSTANCE.engine.loadFont("norm"), DisplayManager.INSTANCE.engine.loadFont("smal"));
		} catch (final IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		userInput = new NormalInputContainer(ic);
		result = new NormalOutputContainer();

		calc.init();
	}

	@Override
	public void init() throws InterruptedException {
		/* Fine caricamento */
	}

	@Override
	public void beforeRender(float dt) {
		if (DisplayManager.INSTANCE.error == null) {
			DisplayManager.INSTANCE.renderer.glClearColor(0xFFc5c2af);
		} else {
			DisplayManager.INSTANCE.renderer.glClearColor(0xFFDC3C32);
		}
		if (userInput.beforeRender(dt)) {
			mustRefresh = true;
		}
		if (computingResult) {
			computingElapsedTime += dt;
			computingAnimationElapsedTime+=dt;
			if (computingAnimationElapsedTime > 0.1) {
				computingAnimationElapsedTime -= 0.1;
				computingAnimationIndex = (computingAnimationIndex + 1) % 16;
				mustRefresh = true;
			}
			if (computingElapsedTime > 5) {
				computingBreakTipVisible = true;
			} 
		} else {
			computingElapsedTime = 0;
			computingAnimationElapsedTime = 0;
			computingAnimationIndex = 0;
			computingBreakTipVisible = false;
		}
	}
	@Override
	public void render() {
		final Renderer renderer = DisplayManager.INSTANCE.renderer;
		fontBig.use(DisplayManager.INSTANCE.engine);
		final int textColor = 0xFF000000;
		final int padding = 4;
		renderer.glColor(textColor);

		userInput.draw(DisplayManager.INSTANCE.engine, renderer, padding, padding + 20);

		if (computingResult) {
			renderer.glColor3f(1, 1, 1);
			final int leftX = 208;
			final int leftY = 16;
			final int size = 32;
			final int posY = computingAnimationIndex % 2;
			final int posX = (computingAnimationIndex - posY) / 2;
			renderer.glFillRect(DisplayManager.INSTANCE.engine.getWidth() - size - 4, DisplayManager.INSTANCE.engine.getHeight() - size - 4, size, size, leftX + size * posX, leftY + size * posY, size, size);
			if (computingBreakTipVisible) {
				Utils.getFont(false).use(DisplayManager.INSTANCE.engine);
				renderer.glColor3f(0.75f, 0, 0);
				renderer.glDrawStringRight(DisplayManager.INSTANCE.engine.getWidth() - 4 - size - 4, DisplayManager.INSTANCE.engine.getHeight() - size/2 - renderer.getCurrentFont().getCharacterHeight()/2 - 4, "Press (=) to stop");
			}
		} else {
			if (!result.isContentEmpty()) {
				result.draw(DisplayManager.INSTANCE.engine, renderer, DisplayManager.INSTANCE.engine.getWidth() - result.getWidth() - 2, DisplayManager.INSTANCE.engine.getHeight() - result.getHeight() - 2);
			}
		}
	}

	@Override
	public void renderStatusbar() {
		final Renderer renderer = DisplayManager.INSTANCE.renderer;
		renderer.glColor3f(1, 1, 1);
		final int pos = 2;
		final int spacersNumb = 1;
		int skinN = 0;
		if (calc.exactMode) {
			skinN = 22;
		} else {
			skinN = 21;
		}
		renderer.glFillRect(2 + 18 * pos + 2 * spacersNumb, 2, 16, 16, 16 * skinN, 16 * 0, 16, 16);
	}


	@Override
	public boolean mustBeRefreshed() {
		if (mustRefresh) {
			mustRefresh = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean keyPressed(Key k) {
		Utils.out.println(1, k.toString());
		try {
			switch (k) {
				case OK:
					userInput.toggleExtra();
					mustRefresh = true;
					return true;
				case HISTORY_BACK:
					if (userInput.isExtraOpened()) {
						userInput.closeExtra();
						currentStep = 0;
						mustRefresh = true;
						return true;
					}
				default:
					if (userInput.isExtraOpened() && userInput.getExtraKeyboardEventListener().keyPressed(k)) {
						currentStep = 0;
						return true;
					} else {
						final boolean step = k == Key.STEP;
						switch (k) {

							case STEP:
								currentStep++;
							case SIMPLIFY:
								if (!step) currentStep = 0;
								if (DisplayManager.INSTANCE.error != null) {
									//TODO: make the error management a global API rather than being relegated to this screen.
									Utils.out.println(1, "Resetting after error...");
									DisplayManager.INSTANCE.error = null;
									calc.f = null;
									calc.f2 = null;
									calc.resultsCount = 0;
									return true;
								} else {
									if (!computingResult) {
										computingResult = true;
										computingThread = new Thread(()-> {
											try {
												try {
													if (!userInput.isAlreadyParsed() && !userInput.isEmpty()) {
														Expression expr = MathParser.parseInput(calc, userInput);
														if (calc.f == null | calc.f2 == null) {
															calc.f = new ObjectArrayList<>();
															calc.f2 = new ObjectArrayList<>();
														} else {
															calc.f.clear();
															calc.f2.clear();
														}
														calc.f.add(expr);
														Utils.out.println(2, "INPUT: " + expr);
														MathSolver ms = new MathSolver(expr);
														ObjectArrayList<ObjectArrayList<Function>> resultSteps = ms.solveAllSteps();
														resultSteps.add(0, Utils.newArrayList(expr));
														ObjectArrayList<Function> resultExpressions = resultSteps.get(resultSteps.size() - 1);	
														for (Function rr : resultExpressions) {
															Utils.out.println(0, "RESULT: " + rr.toString());
														}
														ObjectArrayList<ObjectArrayList<Block>> resultBlocks = MathParser.parseOutput(calc, resultExpressions);
														result.setContentAsMultipleGroups(resultBlocks);
														//									showVariablesDialog(() -> {
														//										currentExpression = newExpression;
														//										simplify();
														//									});
													}
												} catch (final InterruptedException ex) {
													Utils.out.println(Utils.OUTPUTLEVEL_DEBUG_MIN, "Computing thread stopped.");
												} catch (final Exception ex) {
													if (StaticVars.debugOn) {
														ex.printStackTrace();
													}
													throw new Error(Errors.SYNTAX_ERROR);
												}
											} catch (final Error e) {
												final StringWriter sw = new StringWriter();
												final PrintWriter pw = new PrintWriter(sw);
												e.printStackTrace(pw);
												d.errorStackTrace = sw.toString().toUpperCase().replace("\t", "    ").replace("\r", "").split("\n");
												DisplayManager.INSTANCE.error = e.id.toString();
												System.err.println(e.id);
											}
											computingResult = false;
										});
										computingThread.setName("Computing Thread");
										computingThread.setDaemon(true);
										computingThread.setPriority(Thread.NORM_PRIORITY + 3);
										computingThread.start();
										return true;
									} else {
										if (computingThread != null) {
											computingThread.interrupt();
											computingResult = false;
											return true;
										}
										return false;
									}
								}
							case NUM0:
								typeChar('0');
								return true;
							case NUM1:
								typeChar('1');
								return true;
							case NUM2:
								typeChar('2');
								return true;
							case NUM3:
								typeChar('3');
								return true;
							case NUM4:
								typeChar('4');
								return true;
							case NUM5:
								typeChar('5');
								return true;
							case NUM6:
								typeChar('6');
								return true;
							case NUM7:
								typeChar('7');
								return true;
							case NUM8:
								typeChar('8');
								return true;
							case NUM9:
								typeChar('9');
								return true;
							case PLUS:
								typeChar('+');
								return true;
							case MINUS:
								typeChar('-');
								return true;
							case PLUS_MINUS:
								typeChar('±');
								return true;
							case MULTIPLY:
								typeChar('*');
								return true;
							case DIVIDE:
								typeChar('/');
								return true;
							case PARENTHESIS_OPEN:
								typeChar('(');
								return true;
							case PARENTHESIS_CLOSE:
								typeChar(')');
								return true;
							case DOT:
								typeChar('.');
								return true;
							case EQUAL:
								typeChar('=');
								return true;
							case SQRT:
								typeChar('Ⓐ');
								return true;
							case ROOT:
								typeChar('√');
								return true;
							case POWER_OF_2:
								typeChar(MathematicalSymbols.POWER_OF_TWO);
								return true;
							case POWER_OF_x:
								typeChar(MathematicalSymbols.POWER);
								return true;
							case PI:
								typeChar(MathematicalSymbols.PI);
								return true;
							case LETTER_X:
								typeChar(MathematicalSymbols.variables[23]);
								return true;
							case LETTER_Y:
								typeChar(MathematicalSymbols.variables[24]);
								return true;
							case SINE:
								typeChar(MathematicalSymbols.SINE);
								return true;
							case COSINE:
								typeChar(MathematicalSymbols.COSINE);
								return true;
							case TANGENT:
								typeChar(MathematicalSymbols.TANGENT);
								return true;
							case ARCSINE:
								typeChar(MathematicalSymbols.ARC_SINE);
								return true;
							case ARCCOSINE:
								typeChar(MathematicalSymbols.ARC_COSINE);
								return true;
							case ARCTANGENT:
								typeChar(MathematicalSymbols.ARC_TANGENT);
								return true;
							case DELETE:
								userInput.del();
								currentStep = 0;
								mustRefresh = true;
								return true;
							case LEFT:
								userInput.moveLeft();
								mustRefresh = true;
								return true;
							case RIGHT:
								userInput.moveRight();
								mustRefresh = true;
								return true;
							case RESET:
								userInput.clear();
								result.clear();
								currentStep = 0;
								if (DisplayManager.INSTANCE.error != null) {
									Utils.out.println(1, "Resetting after error...");
									DisplayManager.INSTANCE.error = null;
								}
								return true;
							case SURD_MODE:
								calc.exactMode = !calc.exactMode;
								result.clear();
								currentStep = 0;
								Keyboard.keyPressed(Key.SIMPLIFY);
								return true;
							case debug1:
								DisplayManager.INSTANCE.setScreen(new EmptyScreen());
								return true;
							case HISTORY_BACK:
								//					if (DisplayManager.INSTANCE.canGoBack()) {
								//						if (currentExpression != null && currentExpression.length() > 0 & DisplayManager.INSTANCE.sessions[DisplayManager.INSTANCE.currentSession + 1] instanceof MathInputScreen) {
								//							newExpression = currentExpression;
								//							try {
								//								interpreta(true);
								//							} catch (final Error e) {}
								//						}
								//					}
								return false;
							case HISTORY_FORWARD:
								//					if (DisplayManager.INSTANCE.canGoForward()) {
								//						if (currentExpression != null && currentExpression.length() > 0 & DisplayManager.INSTANCE.sessions[DisplayManager.INSTANCE.currentSession - 1] instanceof MathInputScreen) {
								//							newExpression = currentExpression;
								//							try {
								//								interpreta(true);
								//							} catch (final Error e) {}
								//						}
								//					}
								return false;
							case debug_DEG:
								if (calc.angleMode.equals(AngleMode.DEG) == false) {
									calc.angleMode = AngleMode.DEG;
									currentStep = 0;
									return true;
								}
								return false;
							case debug_RAD:
								if (calc.angleMode.equals(AngleMode.RAD) == false) {
									calc.angleMode = AngleMode.RAD;
									currentStep = 0;
									return true;
								}
								return false;
							case debug_GRA:
								if (calc.angleMode.equals(AngleMode.GRA) == false) {
									calc.angleMode = AngleMode.GRA;
									currentStep = 0;
									return true;
								}
								return false;
							case DRG_CYCLE:
								if (calc.angleMode.equals(AngleMode.DEG) == true) {
									calc.angleMode = AngleMode.RAD;
								} else if (calc.angleMode.equals(AngleMode.RAD) == true) {
									calc.angleMode = AngleMode.GRA;
								} else {
									calc.angleMode = AngleMode.DEG;
								}
								currentStep = 0;
								return true;
							default:
								return false;
						}
					}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return true;
		}
	}

	@SuppressWarnings("unused")
	@Deprecated
	private ObjectArrayList<Function> solveExpression(ObjectArrayList<Function> f22) {
		return null;
		/*
		try {
			try {
				return calc.solveExpression(f22);
			} catch (final Exception ex) {
				if (Utils.debugOn) {
					ex.printStackTrace();
				}
				throw new Error(Errors.SYNTAX_ERROR);
			}
		} catch (final Error e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			d.errorStackTrace = sw.toString().toUpperCase().replace("\t", "    ").replace("\r", "").split("\n");
			DisplayManager.INSTANCE.error = e.id.toString();
			System.err.println(e.id);
		}
		return null;
		*/
	}

	@Deprecated
	protected void step() {
		/*
		try {
			try {
				showVariablesDialog();
				ObjectArrayList<Function> results = new ObjectArrayList<>();
				final ObjectArrayList<Function> partialResults = new ObjectArrayList<>();
				for (final Function f : calc.f2) {
					if (f instanceof Equation) {
						DisplayManager.INSTANCE.setScreen(new SolveEquationScreen(this));
					} else {
						results.add(f);
						for (final Function itm : results) {
							if (itm.isSimplified() == false) {
								final List<Function> dt = itm.simplify();
								partialResults.addAll(dt);
							} else {
								partialResults.add(itm);
							}
						}
						results = new ObjectArrayList<>(partialResults);
						partialResults.clear();
					}
				}
		
				if (results.size() == 0) {
					calc.resultsCount = 0;
				} else {
					calc.resultsCount = results.size();
					Collections.reverse(results);
					// add elements to al, including duplicates
					final Set<Function> hs = new LinkedHashSet<>();
					hs.addAll(results);
					results.clear();
					results.addAll(hs);
					calc.f2 = results;
				}
				Utils.out.println(1, calc.f2.toString());
			} catch (final Exception ex) {
				if (Utils.debugOn) {
					ex.printStackTrace();
				}
				throw new Error(Errors.SYNTAX_ERROR);
			}
		} catch (final Error e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			d.errorStackTrace = sw.toString().toUpperCase().replace("\t", "    ").replace("\r", "").split("\n");
			DisplayManager.INSTANCE.error = e.id.toString();
			System.err.println(e.id);
		}
		*/
	}

	@Deprecated
	protected void simplify() {
		/*
		try {
			try {
				for (final Function f : calc.f) {
					if (f instanceof Equation) {
						DisplayManager.INSTANCE.setScreen(new SolveEquationScreen(this));
						return;
					}
				}
		
				final ObjectArrayList<Function> results = solveExpression(calc.f);
				if (results.size() == 0) {
					calc.resultsCount = 0;
				} else {
					calc.resultsCount = results.size();
					Collections.reverse(results);
					// add elements to al, including duplicates
					final Set<Function> hs = new LinkedHashSet<>();
					hs.addAll(results);
					results.clear();
					results.addAll(hs);
					calc.f2 = results;
				}
			} catch (final Exception ex) {
				if (Utils.debugOn) {
					ex.printStackTrace();
				}
				throw new Error(Errors.SYNTAX_ERROR);
			}
		} catch (final Error e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			d.errorStackTrace = sw.toString().toUpperCase().replace("\t", "    ").replace("\r", "").split("\n");
			DisplayManager.INSTANCE.error = e.id.toString();
			System.err.println(e.id);
		}
		*/
	}

	@SuppressWarnings("unused")
	@Deprecated
	private void changeEquationScreen() {
		throw new UnsupportedOperationException();
//		
//		if (!userInput.isEmpty()) {
//			final MathInputScreen cloned = clone();
//			cloned.userInput.setCaretPosition(cloned.userInput.getCaretMaxPosition()-1);
//			DisplayManager.INSTANCE.replaceScreen(cloned);
//			initialized = false;
//			DisplayManager.INSTANCE.setScreen(this);
//
//		}
	}

	public void typeChar(char chr) {
		userInput.typeChar(chr);
		mustRefresh = true;
	}

	@Override
	public boolean keyReleased(Key k) {
		if (k == Key.OK) {
			return true;
		} else {
			if (userInput.isExtraOpened() && userInput.getExtraKeyboardEventListener().keyReleased(k)) {
				return true;
			} else {
				switch (k) {
					default:
						return false;
				}
			}
		}
	}

	public void showVariablesDialog() {
		showVariablesDialog(null);
	}

	public void showVariablesDialog(final Runnable runnable) {
		final Thread ct = new Thread(() -> {
			final ObjectArrayList<Function> knownVarsInFunctions = getKnownVariables(calc.f.toArray(new Function[calc.f.size()]));
			for (final VariableValue f : calc.variablesValues) {
				if (knownVarsInFunctions.contains(f.v)) {
					knownVarsInFunctions.remove(f.v);
				}
			}

			boolean cancelled = false;
			for (final Function f : knownVarsInFunctions) {
				final ChooseVariableValueScreen cvs = new ChooseVariableValueScreen(this, new VariableValue((Variable) f, new Number(calc, 0)));
				DisplayManager.INSTANCE.setScreen(cvs);
				try {
					DisplayManager.INSTANCE.screenChange.acquire();
				} catch (final InterruptedException e) {}
				if (cvs.resultNumberValue == null) {
					cancelled = true;
					break;
				} else {
					final int is = calc.variablesValues.size();
					for (int i = 0; i < is; i++) {
						if (calc.variablesValues.get(i).v == f) {
							calc.variablesValues.remove(i);
						}
					}
					calc.variablesValues.add(new VariableValue((Variable) f, (Number) cvs.resultNumberValue));
				}
			}
			if (!cancelled) {
				if (runnable != null) {
					runnable.run();
				}
			}
		});
		ct.setName("Variables user-input queue thread");
		ct.setPriority(Thread.MIN_PRIORITY);
		ct.setDaemon(true);
		ct.start();
	}

	private ObjectArrayList<Function> getKnownVariables(Function[] fncs) {
		final ObjectArrayList<Function> res = new ObjectArrayList<>();
		for (final Function f : fncs) {
			if (f instanceof FunctionOperator) {
				res.addAll(getKnownVariables(new Function[] { ((FunctionOperator) f).getParameter1(), ((FunctionOperator) f).getParameter2() }));
			} else if (f instanceof FunctionDynamic) {
				res.addAll(getKnownVariables(((FunctionDynamic) f).getParameters()));
			} else if (f instanceof FunctionSingle) {
				res.addAll(getKnownVariables(new Function[] { ((FunctionSingle) f).getParameter() }));
			} else if (f instanceof Variable) {
				if (((Variable) f).getType() == Variable.V_TYPE.CONSTANT) {
					if (!res.contains(f)) {
						res.add(f);
					}
				}
			}
		}
		return res;
	}

	@Override
	@Deprecated
	public MathInputScreen clone() {
		throw new UnsupportedOperationException();
//		final MathInputScreen es = this;
//		final MathInputScreen es2 = new MathInputScreen();
//		es2.errorLevel = es.errorLevel;
//		es2.mustRefresh = es.mustRefresh;
//		es2.calc = Utils.cloner.deepClone(es.calc);
//		es2.userInput = Utils.cloner.deepClone(es.userInput);
//		es2.result = Utils.cloner.deepClone(es.result);
//		return es2;
	}

}
