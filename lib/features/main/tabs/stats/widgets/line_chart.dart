import 'dart:typed_data';

import 'package:fl_chart/fl_chart.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';

class LineChartSample2 extends StatefulWidget {
  const LineChartSample2({super.key, required this.spots, required this.icons});

  final List<FlSpot> spots;
  // final List<Uint8List> icons;
  final List<String> icons;

  @override
  State<LineChartSample2> createState() => _LineChartSample2State();
}

class _LineChartSample2State extends State<LineChartSample2> {
  List<Color> gradientColors = [
    Colors.brown.shade100,
    Colors.white,
  ];

  bool showAvg = false;

  late double minX;
  late double maxX;
  @override
  void initState() {
    super.initState();
    minX = 0;
    maxX = widget.spots.length.toDouble();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 250,
      width: double.infinity,
      child: Listener(
        onPointerSignal: (signal) {
          if (signal is PointerScrollEvent) {
            print(signal);
            setState(() {
              if (signal.scrollDelta.dy.isNegative) {
                minX += maxX * 0.05;
                maxX -= maxX * 0.05;
              } else {
                minX -= maxX * 0.05;
                maxX += maxX * 0.05;
              }
            });
          }
        },
        child: GestureDetector(
          onDoubleTap: () {
            setState(() {
              minX = 0;
              maxX = 30.0;
            });
          },
          onHorizontalDragUpdate: (dragUpdDet) {
            setState(() {
              print(dragUpdDet.primaryDelta);
              double primDelta = dragUpdDet.primaryDelta ?? 0.0;
              if (primDelta != 0) {
                if (primDelta.isNegative) {
                  minX += maxX * 0.005;
                  maxX += maxX * 0.005;
                } else {
                  minX -= maxX * 0.005;
                  maxX -= maxX * 0.005;
                }
              }
            });
          },
          child: LineChart(
            mainData(),
          ),
        ),
      ),
    );
  }

  LineChartData mainData() {
    return LineChartData(
      gridData: const FlGridData(
        show: false,
        drawVerticalLine: false,
        horizontalInterval: 1,
        verticalInterval: 1,
      ),
      titlesData: FlTitlesData(
        show: true,
        rightTitles: const AxisTitles(
          sideTitles: SideTitles(showTitles: false),
        ),
        topTitles: const AxisTitles(
          sideTitles: SideTitles(showTitles: false),
        ),
        bottomTitles: AxisTitles(
          sideTitles: SideTitles(
            showTitles: true,
            reservedSize: 30,
            interval: 1,
            getTitlesWidget: (value, titleMeta) {
              // Get the index for the icon from the value (x-axis)
              int index = value.toInt();
              // Ensure the index doesn't go out of range for the icons list
              if (index >= 0 && index < widget.icons.length) {
                return Padding(
                  padding: const EdgeInsets.only(bottom: 8.0),
                  child: Text(widget.icons[index]),
                  // child: Image.memory(
                  //     widget.icons[index]), // Use the icon from the list
                );
              } else {
                return SizedBox(); // Return empty if index is out of range
              }
            },
            // getTitlesWidget: bottomTitleWidgets,
          ),
        ),
        leftTitles: const AxisTitles(
          sideTitles: SideTitles(showTitles: false),
        ),
      ),
      borderData: FlBorderData(
        show: false,
      ),
      minX: minX,
      maxX: maxX,
      minY: 0,
      maxY: 6,
      lineBarsData: [
        LineChartBarData(
          spots: widget.spots,
          isCurved: true,
          barWidth: 1,
          color: Colors.brown,
          isStrokeCapRound: true,
          dotData: const FlDotData(
            show: false,
          ),
          belowBarData: BarAreaData(
            show: true,
            gradient: LinearGradient(
              begin: Alignment.topCenter, // Starts at the top
              end: Alignment.bottomCenter, // Ends at the bottom
              colors: gradientColors,
            ),
          ),
        ),
      ],
    );
  }
}
