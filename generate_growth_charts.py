import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

sns.set_theme(style="whitegrid", font_scale=1.1)

df = pd.read_csv('sorting_results.csv')

sizes = [20, 100, 1000]
algorithms = ['Selection Sort', 'Insertion Sort', 'Bubble Sort', 
              'Merge Sort', 'Heap Sort', 'Quick Sort']

runtime_data = {}
comparison_data = {}

for algo in algorithms:
    runtime_data[algo] = []
    comparison_data[algo] = []
    for size in sizes:
        subset = df[(df['Algorithm'] == algo) & (df['Array Size'] == size)]
        if not subset.empty:
            runtime_data[algo].append(subset['Avg (ms)'].values[0])
            comparison_data[algo].append(subset['Comparisons'].values[0])

fig, ax = plt.subplots(figsize=(10, 6))
colors = ['#e74c3c', '#e67e22', '#f1c40f', '#2ecc71', '#3498db', '#9b59b6']

for i, algo in enumerate(algorithms):
    if runtime_data[algo]:
        ax.plot(sizes[:len(runtime_data[algo])], runtime_data[algo], 
                marker='o', linewidth=2, markersize=8, 
                label=algo, color=colors[i])

ax.set_xlabel('Array Size (n)', fontsize=12)
ax.set_ylabel('Average Runtime (ms)', fontsize=12)
ax.set_title('Runtime Growth as Array Size Increases', fontsize=14, fontweight='bold')
ax.legend(loc='upper left')
ax.grid(True, alpha=0.3)
ax.set_xscale('log')
ax.set_yscale('log')
plt.tight_layout()
plt.savefig('charts/8_runtime_growth.png', dpi=150, bbox_inches='tight')
print("Saved: charts/8_runtime_growth.png")

fig, ax = plt.subplots(figsize=(10, 6))

for i, algo in enumerate(algorithms):
    if comparison_data[algo]:
        ax.plot(sizes[:len(comparison_data[algo])], comparison_data[algo], 
                marker='s', linewidth=2, markersize=8, 
                label=algo, color=colors[i])

ax.set_xlabel('Array Size (n)', fontsize=12)
ax.set_ylabel('Number of Comparisons', fontsize=12)
ax.set_title('Comparison Count Growth as Array Size Increases', fontsize=14, fontweight='bold')
ax.legend(loc='upper left')
ax.grid(True, alpha=0.3)
ax.set_xscale('log')
ax.set_yscale('log')
plt.tight_layout()
plt.savefig('charts/9_comparison_growth.png', dpi=150, bbox_inches='tight')
print("Saved: charts/9_comparison_growth.png")

print("\nGrowth charts generated successfully!")
