# PowerShell script to seed the backend via GraphQL
param(
  [string]$BackendUrl = "https://rezeptappbackend-a9a2cded5f95.herokuapp.com/graphql",
  [string]$DataFile = "$PSScriptRoot/seed-recipes.json"
)

if (!(Test-Path $DataFile)) {
  Write-Error "Data file not found: $DataFile"
  exit 1
}

$recipes = Get-Content -Raw -Path $DataFile | ConvertFrom-Json

# Fetch existing titles to avoid duplicates
$q = '{ "query": "query { recipes { title } }" }'
try {
  $existingResp = Invoke-RestMethod -Method Post -Uri $BackendUrl -ContentType 'application/json' -Body $q
  $existingTitles = @()
  if ($existingResp.data -and $existingResp.data.recipes) {
    $existingTitles = $existingResp.data.recipes | ForEach-Object { $_.title }
  }
} catch {
  Write-Warning "Could not fetch existing recipes. Proceeding without de-duplication. $_"
  $existingTitles = @()
}
$mutation = @'
mutation Create($recipe: RecipeInput!) {
  createRecipe(recipe: $recipe)
}
'@

$created = 0
foreach ($r in $recipes) {
  if ($existingTitles -contains $r.title) {
    Write-Host "Skipping existing recipe: $($r.title)" -ForegroundColor Yellow
    continue
  }
  $payload = @{ query = $mutation; variables = @{ recipe = $r } } | ConvertTo-Json -Depth 10
  try {
    $resp = Invoke-RestMethod -Method Post -Uri $BackendUrl -ContentType 'application/json' -Body $payload
    if ($resp.errors) {
      Write-Warning ("Error creating recipe '{0}': {1}" -f $r.title, ($resp.errors | ConvertTo-Json -Depth 5))
    } else {
      $ok = $resp.data.createRecipe
      if ($ok) { $created++ } else { Write-Warning "Backend returned false for '$($r.title)'" }
    }
  } catch {
    if ($_.Exception.Response -and $_.Exception.Response.ContentLength) {
      $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
      $body = $reader.ReadToEnd()
      Write-Warning ("HTTP error creating recipe '{0}': {1} - Body: {2}" -f $r.title, $_.Exception.Message, $body)
    } else {
      Write-Warning ("HTTP error creating recipe '{0}': {1}" -f $r.title, $_)
    }
  }
}

Write-Host ("Seed complete. Created {0} recipes." -f $created)
