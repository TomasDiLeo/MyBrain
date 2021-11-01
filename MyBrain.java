package brains;

import java.util.List;

import edu.unlam.snake.brain.Brain;
import edu.unlam.snake.engine.Direction;
import edu.unlam.snake.engine.Point;

public class MyBrain extends Brain {

    private int escapingDistance = 5;
    private int maxLength = 15;

    public MyBrain() {
	super("COMPLETAR NOMBRE DE EQUIPO");
    }

    /**
     * Retorna el próximo movimiento que debe hacer la serpiente.
     * 
     * @param head
     *            , la posición de la cabeza
     * @param previous
     *            , la dirección en la que venía moviéndose
     */
    public Direction getDirection(Point head, Direction previous) {

	Point closestFruit = returnClosestFruit(head);
	Point closestEnemie = returnClosestSnake(head);

	// completar con la lógica necesaria para mover la serpiente,
	// intentando comer la mayor cantidad de frutas y sobrevivir
	// el mayor tiempo posible.

	Direction wantedDirection = previous;

	Point turnedLeft = previous.turnLeft().move(head);
	Point turnedRight = previous.turnRight().move(head);
	Point movedPrevious = previous.move(head);

	// SI EL ENEMIGO ESTA CERCA ENTONCES ME ALEJO
	if (distanceBetween(movedPrevious, closestEnemie) < escapingDistance) {
	    if (distanceBetween(movedPrevious, closestEnemie) > distanceBetween(
		    turnedRight, closestEnemie)
		    && distanceBetween(movedPrevious, closestEnemie) > distanceBetween(
			    turnedLeft, closestEnemie)) {
		wantedDirection = previous;
	    } else if (distanceBetween(turnedLeft, closestEnemie) == distanceBetween(
		    turnedRight, closestEnemie)) {

	    } else if (distanceBetween(turnedLeft, closestEnemie) > distanceBetween(
		    turnedRight, closestEnemie)) {
		wantedDirection = previous.turnLeft();
	    } else if (distanceBetween(turnedLeft, closestEnemie) < distanceBetween(
		    turnedRight, closestEnemie)) {
		wantedDirection = previous.turnRight();
	    }
	} else { // SINO BUSCO FRUTA
	    if (getSnakeLength() < maxLength) { // SI LA SNAKE NO SUPERA UN CIERTO
					 // LARGO VOY A COMER FRUTA
		if (distanceBetween(movedPrevious, closestFruit) < distanceBetween(
			turnedRight, closestFruit)
			&& distanceBetween(movedPrevious, closestFruit) < distanceBetween(
				turnedLeft, closestFruit)) {
		    wantedDirection = previous;
		} else if (distanceBetween(turnedLeft, closestFruit) < distanceBetween(
			turnedRight, closestFruit)) {
		    wantedDirection = previous.turnLeft();
		} else if (distanceBetween(turnedLeft, closestFruit) > distanceBetween(
			turnedRight, closestFruit)) {
		    wantedDirection = previous.turnRight();
		} else if (distanceBetween(movedPrevious, closestFruit) > distanceBetween(
			turnedLeft, closestFruit)) {
		    wantedDirection = previous.turnLeft();
		}
	    }

	}

	// EVITAR OBSTACULOS

	wantedDirection = avoidDanger(wantedDirection, previous, head);

	return wantedDirection;

    }

    private int getSnakeLength() {
	/*
	 * @post: devulve el largo del jugador
	 * */
	List<Point> snake = info.getSnake();
	int largo = 0;

	for (Point body : snake) {
	    largo++;
	}

	return largo;
    }

    private boolean[] takenPositions(Direction previous, Point head) {
	/*
	 * @post: devuleve un array de 3 booleanos que representan si la
	 * posision a la izquierda de head, al frente de head o a la derecha de
	 * head esta ocupada con algun obstaculo (el propio snake, un enemigo o
	 * una pared), en ese orden.
	 */

	List<Point> snake = info.getSnake();
	List<List<Point>> enemies = info.getEnemies();
	List<Point> obstacles = info.getObstacles();

	boolean isFrontTaken = false;
	boolean isLeftTaken = false;
	boolean isRightTaken = false;

	Point frontPos = previous.move(head);
	Point leftPos = previous.turnLeft().move(head);
	Point rightPos = previous.turnRight().move(head);

	// Esta mi cuerpo en alguna de las posiciones?
	for (Point myBody : snake) {
	    if (frontPos.getX() == myBody.getX()
		    && frontPos.getY() == myBody.getY()) {
		isFrontTaken = true;
	    }
	    if (leftPos.getX() == myBody.getX()
		    && leftPos.getY() == myBody.getY()) {
		isLeftTaken = true;
	    }
	    if (rightPos.getX() == myBody.getX()
		    && rightPos.getY() == myBody.getY()) {
		isRightTaken = true;
	    }

	}

	// Hay una pared en alguna de las posiciones?
	for (Point obstacle : obstacles) {
	    if (frontPos.getX() == obstacle.getX()
		    && frontPos.getY() == obstacle.getY()) {
		isFrontTaken = true;
	    }
	    if (leftPos.getX() == obstacle.getX()
		    && leftPos.getY() == obstacle.getY()) {
		isLeftTaken = true;
	    }
	    if (rightPos.getX() == obstacle.getX()
		    && rightPos.getY() == obstacle.getY()) {
		isRightTaken = true;
	    }
	}

	// Hay algun enemigo en alguna de las posiciones?
	for (List<Point> enemie : enemies) {
	    for (Point body : enemie) {
		if (frontPos.getX() == body.getX()
			&& frontPos.getY() == body.getY()) {
		    isFrontTaken = true;
		}
		if (leftPos.getX() == body.getX()
			&& leftPos.getY() == body.getY()) {
		    isLeftTaken = true;
		}
		if (rightPos.getX() == body.getX()
			&& rightPos.getY() == body.getY()) {
		    isRightTaken = true;
		}
	    }
	}

	boolean[] positions =
	{ isLeftTaken, isFrontTaken, isRightTaken };

	return positions;
    }

    private Direction avoidDanger(Direction wanted, Direction previous,
	    Point head) {
	/*
	 * @post: devuelvo la direccion a la que tengo que ir para evitar los
	 * obstaculos evitando entrar en una posicion que no tenga salida
	 * inmediata dentro de lo posible
	 */
	boolean[] positions = takenPositions(previous, head);

	boolean isLeftTaken = positions[0];
	boolean isFrontTaken = positions[1];
	boolean isRightTaken = positions[2];

	// Si la posicion no tiene salida inmediata entonces la considero como
	// ocupada
	if (!isLeftTaken) {
	    boolean[] goLeft = takenPositions(previous.turnLeft(), previous
		    .turnLeft().move(head));
	    if (goLeft[0] && goLeft[1] && goLeft[2]) {
		isLeftTaken = true;
	    }
	}

	if (!isFrontTaken) {
	    boolean[] goFront = takenPositions(previous, previous.move(head));
	    if (goFront[0] && goFront[1] && goFront[2]) {
		isFrontTaken = true;
	    }
	}

	if (!isRightTaken) {
	    boolean[] goRight = takenPositions(previous.turnRight(), previous
		    .turnRight().move(head));
	    if (goRight[0] && goRight[1] && goRight[2]) {
		isRightTaken = true;
	    }
	}

	// La direccion que se quiere obtener depende de la disponibilidad de la
	// posicion
	if (wanted == previous.turnLeft()) {
	    if (isLeftTaken) {
		if (isFrontTaken) {
		    wanted = previous.turnRight();
		} else {
		    wanted = previous;
		}
	    }
	} else if (wanted == previous.turnRight()) {
	    if (isRightTaken) {
		if (isFrontTaken) {
		    wanted = previous.turnLeft();
		} else {
		    wanted = previous;
		}
	    }
	} else if (wanted == previous) {
	    if (isFrontTaken) {
		if (isRightTaken) {
		    wanted = previous.turnLeft();
		} else {
		    wanted = previous.turnRight();
		}
	    }
	}

	return wanted;
    }

    private Point returnClosestSnake(Point from) {
	/*
	 * @post: devuelve el punto en donde esta la cabeza del enemigo mas
	 * cercano desde un punto especifico
	 */
	List<List<Point>> enemies = info.getEnemies();
	Point closestSnake = new Point(10000, 10000);

	for (List<Point> enemie : enemies) {
	    if (distanceBetween(from, enemie.get(0)) < distanceBetween(from,
		    closestSnake)) {
		closestSnake = enemie.get(0);
	    }
	}

	return closestSnake;
    }

    private Point returnClosestFruit(Point from) {
	/*
	 * @post: devuelve el punto en donde esta la fruta mas cercana desde un
	 * punto especifico
	 */
	List<Point> fruits = info.getFruits();
	Point closestFruit = new Point(10000, 10000);

	for (Point fruit : fruits) {
	    if (distanceBetween(from, fruit) < distanceBetween(from,
		    closestFruit)) {
		closestFruit = fruit;
	    }
	}

	return closestFruit;
    }

    private int distanceBetween(Point point1, Point point2) {
	/*
	 * @post: devuelve la distancia Manhattan entre dos puntos
	 * */
	int distance = 0;

	distance += Math.abs(point1.getX() - point2.getX());
	distance += Math.abs(point1.getY() - point2.getY());

	return distance;
    }

}
