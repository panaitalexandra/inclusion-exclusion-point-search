# inclusion-exclusion-point-search
# 2D Range Point Counting using Matrix Pre-processing

## 1. Requirement

Given a set of **N** distinct points and a rectangle with sides parallel to the coordinate axes, the objective is to identify and count all points from the set that lie strictly inside the rectangular area.

The solution uses an optimized matrix-based approach to answer range counting queries in constant time $O(1)$ after a pre-processing phase.

---

## 2. Methodology

### Step 1: Coordinate Extraction and Sorting
Two sorted lists are created from the initial set of points **S**:
* **L1:** Points sorted in ascending order by their **x-coordinates**.
* **L2:** Points sorted in ascending order by their **y-coordinates**.

### Step 2: Matrix Construction (Pre-computation)
A matrix **M** of size $(N + 1) \times (N + 1)$ is initialized. This matrix acts as a 2D cumulative frequency table:
1. For each point $P_{j-1}$ in the sorted list **L1**:
   * Find its rank $k$ in the sorted list **L2**.
   * Update the matrix column $j$:
      * For rows $1$ to $k$: Copy values from the previous column.
      * For rows $k+1$ to $N+1$: Increment the value from the previous column by 1.
      
This ensures that $M[i, j]$ represents the number of points that have an x-rank $\le j$ and a y-rank $\le i$.

### Step 3: Binary Search Localization
For any query point $p(x_p, y_p)$, binary search is performed on **L1** and **L2** to find the corresponding indices $(i, j)$ in matrix **M**. The value $Q(p) = M[i, j]$ gives the total count of points located in the bottom-left quadrant relative to $p$.

### Step 4: Inclusion-Exclusion Principle
To find the number of points inside a rectangle defined by corners $A(x_A, y_A)$ and $C(x_C, y_C)$, we normalize the coordinates into a bounding box $[x_1, x_2] \times [y_1, y_2]$.

Using the four corners of the query rectangle ($p_1, p_2, p_3, p_4$), the final count is calculated using the **Inclusion-Exclusion Principle**:
$$\text{Result} = Q(p_3) - Q(p_2) - Q(p_4) + Q(p_1)$$
Where $p_3$ is the top-right and $p_1$ is the bottom-left corner.



---

## 3. Complexity
* **Pre-processing:** $O(N^2)$ to build the matrix and $O(N \log N)$ for sorting.
* **Query Time:** $O(\log N)$ (for binary search) + $O(1)$ (matrix lookup).
* **Space Complexity:** $O(N^2)$ to store the pre-computed matrix.
