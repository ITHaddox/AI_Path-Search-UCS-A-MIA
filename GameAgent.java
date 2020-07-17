import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import java.util.TreeMap;

import javax.swing.Timer;

/****************************************
 * Name: Tyler Haddox Username: NA Problem Set: PS3 Due Date: March 3, 2020
 ***************************************/

class GameAgent {
	/*
	 * Problem Set 3
	 * 
	 * Student code should be contained within this class.
	 * 
	 */

	public static final float td1 = (float) Math.sqrt(200);
	public static final float td2 = 10;
	HashMap<Point, ShadowNode> hm = new HashMap<Point, ShadowNode>(200);
	TreeMap<ShadowNode, Node> tm = new TreeMap<ShadowNode, Node>(new PathComparator());
	ArrayList<Point> spath = new ArrayList<Point>();
	int index = -1;
	int index2 = -1;
	float maxSpeed = 0;
	float xe = 0;
	float ye = 0;
	// My improved heuristic variables. Ignore for UCS and standard A*
	boolean experimentalOn = false;
	int stepIndex = 0;

	public void drawPlan(Graphics g, GameModel m) {
		g.setColor(Color.red);
		index2 = index;
		while (index2 > 0) {
			Point p1 = spath.get(index2);
			Point p2 = spath.get(index2 - 1);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
			index2--;
		}

		for (Map.Entry<ShadowNode, Node> entry : tm.entrySet()) {
			Node n = entry.getValue();
			g.setColor(Color.black);
			g.drawOval((int) n.x, (int) n.y, 10, 10);
		}
	}

	public void update(GameModel m) {
		GameController c = m.getController();
		Graphics g = c.view.getGraphics();
		while (true) {
			MouseEvent e = c.nextMouseEvent();
			if (e == null)
				break;
			if (e.getButton() == 1) {
				System.out.println("Standard UCS (Uniform Cost Search");
				hm.clear();
				tm.clear();
				spath.clear();
				xe = e.getX();
				ye = e.getY();
				UCS(m, g);
				index = spath.size() - 1;
				index2 = spath.size() - 1;
				experimentalOn = false;
			} else if (e.getButton() == 3) {
				System.out.println("Standard A* Search");
				hm.clear();
				tm.clear();
				spath.clear();
				xe = e.getX();
				ye = e.getY();
				AStar(m, g);
				index = spath.size() - 1;
				index2 = spath.size() - 1;
				experimentalOn = false;
			} else if (e.getButton() == 2) {
				// My improved A*. Middle click or scroll wheel click.
				System.out.println("My improved A* Search");
				hm.clear();
				tm.clear();
				xe = e.getX();
				ye = e.getY();
				experimentalOn = true;
				improvedAStar(m, g);
				index = spath.size() - 1;
			}
		}

		/*****************************************************
		 *****************************************************
		 * Regular problem set UCS and A* sprite movement
		 *****************************************************
		 *****************************************************/
		if (!experimentalOn) {
			if (index >= 0 && m.getX() == m.getDestXValue() && m.getY() == m.getDestYValue()) {
				index2 = index;
				Point p = spath.get(index);
				index--;
				m.setDest(p.x, p.y);
			} else if (index < 0) {
				spath.clear();
			}

			/*****************************************************
			 *****************************************************
			 * My improved heuristic A* search sprite movement
			 *****************************************************
			 *****************************************************/
		} else {
			if (index >= 0 && m.getX() == m.getDestXValue() && m.getY() == m.getDestYValue()) {
				if (stepIndex == 5) {
					hm.clear();
					tm.clear();
					spath.clear();
					improvedAStar(m, g);
					index = spath.size() - 1;
					stepIndex = 0;
				}
				index2 = index;
				Point p = spath.get(index);
				index--;
				m.setDest(p.x, p.y);
				stepIndex++;

			} else if (index < 0) {
				spath.clear();
			}
		}
	}

	/*****************************************************
	 *****************************************************
	 * Regular problem set Uniform-Cost Search algorithm
	 *****************************************************
	 *****************************************************/
	public void UCS(GameModel m, Graphics g) {
		Point p = new Point((int) m.getX(), (int) m.getY());
		Node node = new Node(p.x, p.y, m.getSpeedOfTravel(p.x, p.y), 0);
		ShadowNode sn = new ShadowNode(p.x, p.y, 0, null);
		tm.put(sn, node);
		hm.put(p, sn);
		while (true) {
			if (tm.isEmpty()) {
				break;
			}
			node = tm.pollFirstEntry().getValue();
			ShadowNode shnode = hm.get(new Point((int) node.x, (int) node.y));
			shnode.e = true;
			if (node.x <= xe && node.x + 10 > xe && node.y <= ye && node.y + 10 > ye) {
				while (shnode.prev != null) {
					spath.add(new Point((int) shnode.x, (int) shnode.y));
					shnode = hm.get(shnode.prev);
				}
				break;
			}
			for (int i = 0; i < 8; i++) {
				Point pp = node.act[i];
				if (0 <= pp.x && pp.x < 1200 && 0 <= pp.y && pp.y < 600) {
					if (hm.containsKey(pp) && !hm.get(pp).e) {
						ShadowNode ss = hm.get(pp);
						Node nn = tm.get(ss);
						if (i % 2 == 0) {
							nn.setTime(td1);
						} else {
							nn.setTime(td2);
						}
						if (ss.tc > node.tc + nn.t) {
							tm.remove(ss);
							hm.remove(pp);
							nn.tc = node.tc + nn.t;
							ss.prev = new Point((int) node.x, (int) node.y);
							ss.tc = node.tc + nn.t;
							hm.put(pp, ss);
							tm.put(ss, nn);
						}
					} else if (!hm.containsKey(pp)) {
						Node nn = new Node(pp.x, pp.y, m.getSpeedOfTravel(pp.x, pp.y));
						if (i % 2 == 0) {
							nn.setTime(td1);
						} else {
							nn.setTime(td2);
						}
						nn.tc = node.tc + nn.t;
						ShadowNode ss = new ShadowNode(pp.x, pp.y, nn.tc, new Point((int) node.x, (int) node.y));
						hm.put(pp, ss);
						tm.put(ss, nn);
					}
				}
			}
		}
	}

	/*****************************************************
	 *****************************************************
	 * Regular problem set A* Search algorithm
	 *****************************************************
	 *****************************************************/
	public void AStar(GameModel m, Graphics g) {
		Point p = new Point((int) m.getX(), (int) m.getY());
		// float heuristic = getMinT(m.getX(), m.getY(), xe, ye);
		Node node = new Node(p.x, p.y, m.getSpeedOfTravel(p.x, p.y), 0);
		ShadowNode sn = new ShadowNode(p.x, p.y, 0, null);
		tm.put(sn, node);
		hm.put(p, sn);
		while (true) {
			if (tm.isEmpty()) {
				break;
			}
			node = tm.pollFirstEntry().getValue();
			ShadowNode shnode = hm.get(new Point((int) node.x, (int) node.y));
			shnode.e = true;
			if (node.x <= xe && node.x + 10 > xe && node.y <= ye && node.y + 10 > ye) {
				while (shnode.prev != null) {
					spath.add(new Point((int) shnode.x, (int) shnode.y));
					shnode = hm.get(shnode.prev);
				}
				break;
			}
			for (int i = 0; i < 8; i++) {
				Point pp = node.act[i];
				if (0 <= pp.x && pp.x < 1200 && 0 <= pp.y && pp.y < 600) {
					if (hm.containsKey(pp) && !hm.get(pp).e) {
						ShadowNode ss = hm.get(pp);
						Node nn = tm.get(ss);
						if (i % 2 == 0) {
							nn.setTime(td1);
						} else {
							nn.setTime(td2);
						}
						float gn = node.tc + nn.t;
						float hn = getMinT(nn.x, nn.y, xe, ye);
						if (ss.tc > gn + hn) {
							tm.remove(ss);
							hm.remove(pp);
							nn.tc = gn + hn;
							ss.prev = new Point((int) node.x, (int) node.y);
							ss.tc = gn + hn;
							hm.put(pp, ss);
							tm.put(ss, nn);
						}
					} else if (!hm.containsKey(pp)) {
						Node nn = new Node(pp.x, pp.y, m.getSpeedOfTravel(pp.x, pp.y));
						if (i % 2 == 0) {
							nn.setTime(td1);
						} else {
							nn.setTime(td2);
						}
						float gn = node.tc + nn.t;
						float hn = getMinT(nn.x, nn.y, xe, ye);
						nn.tc = gn + hn;
						ShadowNode ss = new ShadowNode(pp.x, pp.y, nn.tc, new Point((int) node.x, (int) node.y));
						hm.put(pp, ss);
						tm.put(ss, nn);
					}
				}
			}
		}
	}

	/****************************************************************
	 ****************************************************************
	 * My improved heuristic A* search algorithm. Works extremely well for searching
	 * less nodes than both standard A* and UCS. The path is not the optimal
	 * solution but it is closer to UCS's optimal solution than A*
	 ****************************************************************
	 ****************************************************************/
	public void improvedAStar(GameModel m, Graphics g) {
		Point p = new Point((int) m.getX(), (int) m.getY());
		Node node = new Node(p.x, p.y, m.getSpeedOfTravel(p.x, p.y), 0);
		ShadowNode sn = new ShadowNode(p.x, p.y, 0, null);
		tm.put(sn, node);
		hm.put(p, sn);
		while (true) {
			if (tm.isEmpty()) {
				break;
			}
			node = tm.pollFirstEntry().getValue();
			ShadowNode shnode = hm.get(new Point((int) node.x, (int) node.y));
			shnode.e = true;
			if (node.x <= xe && node.x + 10 > xe && node.y <= ye && node.y + 10 > ye) {
				while (shnode.prev != null) {
					spath.add(new Point((int) shnode.x, (int) shnode.y));
					shnode = hm.get(shnode.prev);
				}
				break;
			}
			for (int i = 0; i < 8; i++) {
				Point pp = node.act[i];
				if (0 <= pp.x && pp.x < 1200 && 0 <= pp.y && pp.y < 600) {
					if (hm.containsKey(pp) && !hm.get(pp).e) {
						ShadowNode ss = hm.get(pp);
						Node nn = tm.get(ss);
						if (i % 2 == 0) {
							nn.setTime(td1);
						} else {
							nn.setTime(td2);
						}
						float gn = node.tc + nn.t;
						float hn = nn.minT - node.minT;
						if (ss.tc > gn + hn) {
							tm.remove(ss);
							hm.remove(pp);
							nn.tc = gn + hn;
							ss.prev = new Point((int) node.x, (int) node.y);
							ss.tc = gn + hn;
							hm.put(pp, ss);
							tm.put(ss, nn);
						}
					} else if (!hm.containsKey(pp)) {
						Node nn = new Node(pp.x, pp.y, m.getSpeedOfTravel(pp.x, pp.y));
						if (i % 2 == 0) {
							nn.setTime(td1);
						} else {
							nn.setTime(td2);
						}
						float gn = node.tc + nn.t;
						float hn = nn.minT - node.minT;
						nn.tc = gn + hn;
						ShadowNode ss = new ShadowNode(pp.x, pp.y, nn.tc, new Point((int) node.x, (int) node.y));
						hm.put(pp, ss);
						tm.put(ss, nn);
					}
				}
			}
		}

	}

	/*****************************************************
	 *****************************************************
	 * Other methods and user defined objects
	 *****************************************************
	 *****************************************************/

	class PathComparator implements Comparator<ShadowNode> {
		public int compare(ShadowNode a, ShadowNode b) {
			if (a.tc > b.tc) {
				return 1;
			} else if (a.tc < b.tc) {
				return -1;
			} else if (a.p != b.p) {
				if (a.x > b.x) {
					return 1;
				} else if (a.x < b.x) {
					return -1;
				} else if (a.y > b.y) {
					return 1;
				} else if (a.y < b.y) {
					return -1;
				}
			}
			return 0;
		}
	}

	class ShadowNode {
		float x;
		float y;
		float tc;
		Point p;
		Point prev;
		boolean e = false;

		public ShadowNode(float x, float y, float tc, Point prev) {
			this.x = x;
			this.y = y;
			this.tc = tc;
			this.prev = prev;
			this.p = new Point((int) x, (int) y);
		}

		public String toString() {
			String str = ("x:" + x + "   y:" + y + "    tc:" + tc + "   " + e);
			return str;
		}
	}

	class Node {
		float minT;
		float tc;
		float x;
		float y;
		float s;
		float t;
		Point[] act = new Point[8];

		public Node() {
		}

		public Node(float x, float y, float s) {
			this.x = x;
			this.y = y;
			this.s = s;
			setActPoints();
			setMinT();
		}

		public Node(float x, float y, float s, float tc) {
			this.x = x;
			this.y = y;
			this.s = s;
			this.tc = tc;
			setActPoints();
			setMinT();
		}

		private void setActPoints() {
			act[0] = new Point((int) x - 10, (int) y - 10);
			act[1] = new Point((int) x, (int) y - 10);
			act[2] = new Point((int) x + 10, (int) y - 10);
			act[3] = new Point((int) x + 10, (int) y);
			act[4] = new Point((int) x + 10, (int) y + 10);
			act[5] = new Point((int) x, (int) y + 10);
			act[6] = new Point((int) x - 10, (int) y + 10);
			act[7] = new Point((int) x - 10, (int) y);
		}

		public void setMinT() {
			float dis = (float) Math.sqrt(Math.pow((x - xe), 2) + Math.pow((y - ye), 2));
			this.minT = dis / this.s;
		}

		public void setTime(float d) {
			this.t = d / this.s;
		}

		public String toString() {
			String str = ("x:" + x + "   y:" + y + "    tc:" + tc);
			return str;
		}
	}

	public void findMaxSpeed(GameModel m) {
		float k = 0;
		for (int i = 0; i < 1200; i += 10) {
			for (int j = 0; j < 600; j += 10) {
				k = m.getSpeedOfTravel(i, j);
				if (k > maxSpeed) {
					maxSpeed = k;
				}
			}
		}
	}

	public float getMinT(float x, float y, float xe, float ye) {
		float dis = (float) Math.sqrt(Math.pow((x - xe), 2) + Math.pow((y - ye), 2));
		return dis / maxSpeed;
	}

	public static void main(String[] args) throws Exception {
		GameController c = new GameController();
		c.initialize();

		// This will instantiate a new instance of JFrame. Each will spawn in another
		// thread to generate events
		// and keep the entire program running until the JFrame is terminated.
		c.view = new GameView(c, c.model);
		c.agent.findMaxSpeed(c.model);
		// this will create an ActionEvent at fairly regular intervals. Each of the
		// events are handled by
		// GameView.actionPerformed()
		new Timer(20, c.view).start();
	}
}
