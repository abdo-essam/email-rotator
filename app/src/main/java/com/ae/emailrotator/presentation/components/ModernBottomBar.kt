package com.ae.emailrotator.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ae.emailrotator.presentation.theme.Blue500

data class BottomBarTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun ModernBottomBar(
    tabs: List<BottomBarTab>,
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column {
            // Top divider
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    val scale by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.9f,
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "tabScale"
                    )
                    val iconColor by animateColorAsState(
                        targetValue = if (selected)
                            Blue500
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "tabColor"
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .scale(scale)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(tab.route) }
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (selected) {
                                Box(
                                    Modifier
                                        .size(width = 48.dp, height = 28.dp)
                                        .clip(CircleShape)
                                        .background(Blue500.copy(alpha = 0.12f))
                                )
                            }
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.icon,
                                contentDescription = tab.label,
                                tint = iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            tab.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            color = iconColor
                        )
                    }
                }
            }
        }
    }
}
